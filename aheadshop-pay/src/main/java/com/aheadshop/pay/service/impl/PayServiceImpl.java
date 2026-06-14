package com.aheadshop.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.aheadshop.common.core.enums.PayStatus;
import com.aheadshop.common.core.enums.PayType;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.common.core.util.SnowflakeIdWorker;
import com.aheadshop.pay.config.AlipayConfig;
import com.aheadshop.pay.domain.dto.PayCreateDTO;
import com.aheadshop.pay.domain.dto.RefundDTO;
import com.aheadshop.pay.domain.po.PayRecord;
import com.aheadshop.pay.domain.vo.OrderInfoVO;
import com.aheadshop.pay.domain.vo.PaySubmitVO;
import com.aheadshop.pay.feign.OrderFeignClient;
import com.aheadshop.pay.mapper.PayRecordMapper;
import com.aheadshop.pay.service.IPayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements IPayService {

    private final PayRecordMapper payRecordMapper;
    private final OrderFeignClient orderFeignClient;
    private final AlipayConfig alipayConfig;

    @Override
    public PaySubmitVO createPay(Long userId, PayCreateDTO dto) {
        // 1. 校验订单
        Result<OrderInfoVO> orderResult = orderFeignClient.getOrder(dto.getOrderNo());
        if (orderResult == null || orderResult.getCode() != 200 || orderResult.getData() == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        OrderInfoVO order = orderResult.getData();
        if (order.getStatus() != 0) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "订单状态异常，无法支付");
        }

        // 2. 检查是否已有待支付记录（幂等）
        PayRecord existing = payRecordMapper.selectOne(
                new LambdaQueryWrapper<PayRecord>()
                        .eq(PayRecord::getOrderNo, dto.getOrderNo())
                        .eq(PayRecord::getStatus, PayStatus.WAIT_PAY.getCode()));
        if (existing != null) {
            // 已有待支付记录，直接返回
            String payForm = buildAlipayForm(existing.getPayNo(), order, dto.getReturnUrl());
            return PaySubmitVO.builder()
                    .payNo(existing.getPayNo())
                    .payForm(payForm)
                    .build();
        }

        // 3. 创建支付记录
        String payNo = SnowflakeIdWorker.generateOrderNo();
        PayRecord record = new PayRecord();
        record.setPayNo(payNo);
        record.setOrderNo(dto.getOrderNo());
        record.setUserId(userId);
        record.setPayAmount(order.getPayAmount());
        record.setPayType(dto.getPayType());
        record.setStatus(PayStatus.WAIT_PAY.getCode());
        payRecordMapper.insert(record);

        // 4. 调用支付宝下单
        String payForm = buildAlipayForm(payNo, order, dto.getReturnUrl());

        return PaySubmitVO.builder()
                .payNo(payNo)
                .payForm(payForm)
                .build();
    }

    @Override
    public Integer queryPayStatus(String payNo) {
        PayRecord record = payRecordMapper.selectOne(
                new LambdaQueryWrapper<PayRecord>().eq(PayRecord::getPayNo, payNo));
        if (record == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "支付记录不存在");
        }
        return record.getStatus();
    }

    @Override
    public String handleAlipayCallback(Map<String, String> params) {
        try {
            // 1. 验签
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    "utf-8",
                    "RSA2");
            if (!signVerified) {
                log.error("支付宝回调验签失败");
                return "failure";
            }

            // 2. 获取参数
            String tradeNo = params.get("trade_no");
            String outTradeNo = params.get("out_trade_no"); // 我们的 payNo
            String tradeStatus = params.get("trade_status");

            log.info("支付宝回调: tradeNo={}, outTradeNo={}, status={}", tradeNo, outTradeNo, tradeStatus);

            // 3. 查询支付记录
            PayRecord record = payRecordMapper.selectOne(
                    new LambdaQueryWrapper<PayRecord>().eq(PayRecord::getPayNo, outTradeNo));
            if (record == null) {
                log.error("支付记录不存在: {}", outTradeNo);
                return "failure";
            }

            // 4. 幂等检查
            if (record.getStatus() == PayStatus.SUCCESS.getCode()) {
                log.info("支付记录已处理: {}", outTradeNo);
                return "success";
            }

            // 5. 处理支付结果
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                record.setTradeNo(tradeNo);
                record.setStatus(PayStatus.SUCCESS.getCode());
                record.setPayTime(LocalDateTime.now());
                record.setCallbackTime(LocalDateTime.now());
                record.setCallbackContent(params.toString());
                payRecordMapper.updateById(record);

                // 6. 通知订单服务
                try {
                    orderFeignClient.paySuccess(record.getOrderNo());
                    log.info("订单 {} 支付成功通知已发送", record.getOrderNo());
                } catch (Exception e) {
                    log.error("订单 {} 支付成功通知失败: {}", record.getOrderNo(), e.getMessage());
                }
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                record.setStatus(PayStatus.FAILED.getCode());
                record.setCallbackTime(LocalDateTime.now());
                record.setCallbackContent(params.toString());
                payRecordMapper.updateById(record);
            }

            return "success";
        } catch (AlipayApiException e) {
            log.error("支付宝回调处理异常: {}", e.getMessage());
            return "failure";
        }
    }

    @Override
    public void refund(RefundDTO dto) {
        // 1. 查询支付记录
        PayRecord record = payRecordMapper.selectOne(
                new LambdaQueryWrapper<PayRecord>()
                        .eq(PayRecord::getOrderNo, dto.getOrderNo())
                        .eq(PayRecord::getStatus, PayStatus.SUCCESS.getCode()));
        if (record == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "支付记录不存在或状态异常");
        }

        // 2. 调用支付宝退款 API
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    "json", "utf-8",
                    alipayConfig.getAlipayPublicKey(),
                    "RSA2");

            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizContent("{\"trade_no\":\"" + record.getTradeNo() + "\","
                    + "\"refund_amount\":\"" + dto.getRefundAmount() + "\","
                    + "\"out_request_no\":\"" + record.getPayNo() + "\","
                    + "\"refund_reason\":\"" + (dto.getRefundReason() != null ? dto.getRefundReason() : "用户申请退款") + "\"}");

            var response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                log.error("支付宝退款失败: {}", response.getSubMsg());
                throw new BusinessException(BusinessExceptionCode.PAY_CREATE_FAIL, "退款申请失败: " + response.getSubMsg());
            }
            log.info("支付宝退款申请成功: orderNo={}, tradeNo={}", dto.getOrderNo(), record.getTradeNo());
        } catch (AlipayApiException e) {
            log.error("支付宝退款异常: {}", e.getMessage());
            throw new BusinessException(BusinessExceptionCode.PAY_CREATE_FAIL, "退款申请失败");
        }

        // 3. 更新支付记录状态为退款中
        record.setStatus(PayStatus.REFUNDED.getCode());
        payRecordMapper.updateById(record);

        // 4. 通知订单服务
        try {
            orderFeignClient.refundSuccess(record.getOrderNo());
            log.info("订单 {} 退款通知已发送", record.getOrderNo());
        } catch (Exception e) {
            log.error("订单 {} 退款通知失败: {}", record.getOrderNo(), e.getMessage());
        }
    }

    @Override
    public String handleRefundCallback(Map<String, String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params, alipayConfig.getAlipayPublicKey(), "utf-8", "RSA2");
            if (!signVerified) {
                log.error("支付宝退款回调验签失败");
                return "failure";
            }

            String outTradeNo = params.get("out_trade_no");
            log.info("支付宝退款回调: outTradeNo={}", outTradeNo);

            PayRecord record = payRecordMapper.selectOne(
                    new LambdaQueryWrapper<PayRecord>().eq(PayRecord::getPayNo, outTradeNo));
            if (record == null) {
                log.error("支付记录不存在: {}", outTradeNo);
                return "failure";
            }

            // 幂等检查
            if (record.getStatus() == PayStatus.REFUNDED.getCode()) {
                return "success";
            }

            record.setStatus(PayStatus.REFUNDED.getCode());
            record.setCallbackTime(LocalDateTime.now());
            record.setCallbackContent(params.toString());
            payRecordMapper.updateById(record);

            return "success";
        } catch (AlipayApiException e) {
            log.error("支付宝退款回调处理异常: {}", e.getMessage());
            return "failure";
        }
    }

    private String buildAlipayForm(String payNo, OrderInfoVO order, String returnUrl) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    "json",
                    "utf-8",
                    alipayConfig.getAlipayPublicKey(),
                    "RSA2");

            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            // 优先使用前端传入的 returnUrl，否则用配置文件默认值
            request.setReturnUrl(returnUrl != null && !returnUrl.isEmpty() ? returnUrl : alipayConfig.getReturnUrl());
            request.setNotifyUrl(alipayConfig.getNotifyUrl());

            request.setBizContent("{\"out_trade_no\":\"" + payNo + "\","
                    + "\"total_amount\":\"" + order.getPayAmount() + "\","
                    + "\"subject\":\"AheadShop订单-" + order.getOrderNo() + "\","
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

            return alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            log.error("支付宝下单异常: {}", e.getMessage());
            throw new BusinessException(BusinessExceptionCode.PAY_CREATE_FAIL, "创建支付失败");
        }
    }
}
