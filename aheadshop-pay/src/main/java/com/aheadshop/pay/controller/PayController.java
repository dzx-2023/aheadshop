package com.aheadshop.pay.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.pay.domain.dto.PayCreateDTO;
import com.aheadshop.pay.domain.dto.RefundDTO;
import com.aheadshop.pay.domain.vo.PaySubmitVO;
import com.aheadshop.pay.service.IPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
@Tag(name = "支付接口")
public class PayController {

    private final IPayService payService;

    @Operation(summary = "创建支付")
    @PostMapping("/create")
    public Result<PaySubmitVO> create(@RequestHeader("X-User-Id") Long userId,
                                      @RequestBody PayCreateDTO dto) {
        return Result.success(payService.createPay(userId, dto));
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/status/{payNo}")
    public Result<Integer> status(@PathVariable("payNo") String payNo) {
        return Result.success(payService.queryPayStatus(payNo));
    }

    @Operation(summary = "支付宝异步回调")
    @PostMapping("/callback/alipay")
    public String alipayCallback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        return payService.handleAlipayCallback(params);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund")
    public Result<Void> refund(@RequestHeader("X-User-Id") Long userId,
                               @Valid @RequestBody RefundDTO dto) {
        payService.refund(dto);
        return Result.success(null);
    }

    @Operation(summary = "支付宝退款回调")
    @PostMapping("/callback/refund")
    public String refundCallback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        return payService.handleRefundCallback(params);
    }
}
