package com.aheadshop.order.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.order.domain.vo.RefundPageVO;
import com.aheadshop.order.service.IRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/refund")
@RequiredArgsConstructor
@Tag(name = "退款内部接口（供管理服务调用）")
public class InnerRefundController {

    private final IRefundService refundService;

    @Operation(summary = "退款列表")
    @GetMapping("/list")
    public Result<PageResult<RefundPageVO>> list(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(refundService.listRefunds(status, pageNum, pageSize));
    }

    @Operation(summary = "审批退款")
    @PutMapping("/audit/{refundId}")
    public Result<Void> audit(@PathVariable("refundId") Long refundId,
                              @RequestParam("approved") boolean approved,
                              @RequestParam(value = "remark", required = false) String remark) {
        refundService.auditRefund(refundId, approved, remark);
        return Result.success(null);
    }

    @Operation(summary = "根据订单号查询退款")
    @GetMapping("/order/{orderNo}")
    public Result<RefundPageVO> getByOrderNo(@PathVariable("orderNo") String orderNo) {
        return Result.success(refundService.getRefundByOrderNo(orderNo));
    }
}
