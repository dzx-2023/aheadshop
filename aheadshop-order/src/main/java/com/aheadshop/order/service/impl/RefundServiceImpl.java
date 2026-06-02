package com.aheadshop.order.service.impl;

import com.aheadshop.common.core.enums.OrderStatus;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.common.core.util.SnowflakeIdWorker;
import com.aheadshop.order.domain.po.Order;
import com.aheadshop.order.domain.po.Refund;
import com.aheadshop.order.domain.vo.RefundPageVO;
import com.aheadshop.order.feign.PayFeignClient;
import com.aheadshop.order.mapper.OrderMapper;
import com.aheadshop.order.mapper.RefundMapper;
import com.aheadshop.order.service.IRefundService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements IRefundService {

    private final RefundMapper refundMapper;
    private final OrderMapper orderMapper;
    private final PayFeignClient payFeignClient;

    @Override
    @Transactional
    public void createRefund(Long userId, String orderNo, Integer refundType, String reason) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(BusinessExceptionCode.FORBIDDEN, "无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()
                && order.getStatus() != OrderStatus.SHIPPED.getCode()
                && order.getStatus() != OrderStatus.RECEIVED.getCode()
                && order.getStatus() != OrderStatus.COMPLETED.getCode()) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "当前订单状态不可申请退款");
        }

        Refund refund = new Refund();
        refund.setRefundNo(String.valueOf(SnowflakeIdWorker.getInstance().nextId()));
        refund.setOrderNo(orderNo);
        refund.setUserId(userId);
        refund.setRefundAmount(order.getPayAmount());
        refund.setRefundReason(reason);
        refund.setRefundType(refundType);
        refund.setStatus(0);
        refundMapper.insert(refund);

        order.setStatus(OrderStatus.REFUNDING.getCode());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void auditRefund(Long refundId, boolean approved, String remark) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "退款单不存在");
        }
        if (refund.getStatus() != 0) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "退款单已处理");
        }

        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, refund.getOrderNo()));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }

        if (approved) {
            // 1. 先标记为退款中
            refund.setStatus(2); // 退款中
            refund.setAuditRemark(remark);
            refundMapper.updateById(refund);
            order.setStatus(OrderStatus.REFUNDING.getCode());
            orderMapper.updateById(order);

            // 2. 调用支付服务发起支付宝退款
            try {
                Result<Void> result = payFeignClient.refund(
                        refund.getOrderNo(), refund.getRefundAmount(), refund.getRefundReason());
                if (result != null && result.getCode() == 200) {
                    // 退款成功
                    refund.setStatus(3); // 已退款
                    order.setStatus(OrderStatus.REFUNDED.getCode());
                    log.info("订单 {} 退款成功", refund.getOrderNo());
                } else {
                    // 退款失败，保持退款中状态，等待回调或人工处理
                    log.error("订单 {} 退款调用返回异常: {}", refund.getOrderNo(),
                            result != null ? result.getMsg() : "null result");
                }
            } catch (Exception e) {
                // 退款调用失败，保持退款中状态
                log.error("订单 {} 退款调用失败: {}", refund.getOrderNo(), e.getMessage());
            }
        } else {
            refund.setStatus(4); // 已拒绝
            refund.setAuditRemark(remark);
            // 拒绝退款后恢复订单状态
            order.setStatus(order.getPayTime() != null ? OrderStatus.PAID.getCode() : OrderStatus.CANCELLED.getCode());
        }
        refundMapper.updateById(refund);
        orderMapper.updateById(order);
    }

    @Override
    public PageResult<RefundPageVO> listRefunds(Integer status, Integer pageNum, Integer pageSize) {
        Page<Refund> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Refund::getStatus, status);
        }
        wrapper.orderByDesc(Refund::getCreateTime);
        Page<Refund> result = refundMapper.selectPage(page, wrapper);

        return PageResult.of(result.getTotal(),
                result.getRecords().stream().map(this::toVO).toList());
    }

    @Override
    public PageResult<RefundPageVO> listUserRefunds(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Refund> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Refund::getUserId, userId);
        if (status != null) {
            wrapper.eq(Refund::getStatus, status);
        }
        wrapper.orderByDesc(Refund::getCreateTime);
        Page<Refund> result = refundMapper.selectPage(page, wrapper);

        return PageResult.of(result.getTotal(),
                result.getRecords().stream().map(this::toVO).toList());
    }

    @Override
    public RefundPageVO getRefundByOrderNo(String orderNo) {
        Refund refund = refundMapper.selectOne(
                new LambdaQueryWrapper<Refund>().eq(Refund::getOrderNo, orderNo));
        return refund != null ? toVO(refund) : null;
    }

    private RefundPageVO toVO(Refund refund) {
        return RefundPageVO.builder()
                .id(refund.getId())
                .refundNo(refund.getRefundNo())
                .orderNo(refund.getOrderNo())
                .userId(refund.getUserId())
                .refundAmount(refund.getRefundAmount())
                .refundReason(refund.getRefundReason())
                .refundType(refund.getRefundType())
                .status(refund.getStatus())
                .auditRemark(refund.getAuditRemark())
                .createTime(refund.getCreateTime())
                .build();
    }
}
