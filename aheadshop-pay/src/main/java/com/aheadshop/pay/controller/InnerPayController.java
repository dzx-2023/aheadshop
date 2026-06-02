package com.aheadshop.pay.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.pay.domain.dto.RefundDTO;
import com.aheadshop.pay.service.IPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/inner/pay")
@RequiredArgsConstructor
@Tag(name = "支付内部接口（供订单服务调用）")
public class InnerPayController {

    private final IPayService payService;

    @Operation(summary = "发起退款（内部调用）")
    @PostMapping("/refund")
    public Result<Void> refund(@RequestParam("orderNo") String orderNo,
                               @RequestParam("refundAmount") BigDecimal refundAmount,
                               @RequestParam(value = "refundReason", required = false) String refundReason) {
        RefundDTO dto = new RefundDTO();
        dto.setOrderNo(orderNo);
        dto.setRefundAmount(refundAmount);
        dto.setRefundReason(refundReason);
        payService.refund(dto);
        return Result.success(null);
    }
}
