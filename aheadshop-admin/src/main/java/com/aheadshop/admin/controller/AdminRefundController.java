package com.aheadshop.admin.controller;

import com.aheadshop.admin.domain.vo.RefundPageVO;
import com.aheadshop.admin.feign.RefundFeignClient;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/refund")
@RequiredArgsConstructor
@Tag(name = "管理端-退款管理")
public class AdminRefundController {

    private final RefundFeignClient refundFeignClient;

    @Operation(summary = "退款列表")
    @GetMapping("/list")
    public Result<PageResult<RefundPageVO>> list(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return refundFeignClient.listRefunds(status, pageNum, pageSize);
    }

    @Operation(summary = "审批退款")
    @PutMapping("/audit/{refundId}")
    public Result<Void> audit(@PathVariable("refundId") Long refundId,
                              @RequestParam("approved") boolean approved,
                              @RequestParam(value = "remark", required = false) String remark) {
        return refundFeignClient.auditRefund(refundId, approved, remark);
    }

    @Operation(summary = "根据订单号查询退款")
    @GetMapping("/order/{orderNo}")
    public Result<RefundPageVO> getByOrderNo(@PathVariable("orderNo") String orderNo) {
        return refundFeignClient.getRefundByOrderNo(orderNo);
    }
}
