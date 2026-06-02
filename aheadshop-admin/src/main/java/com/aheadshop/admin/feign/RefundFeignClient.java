package com.aheadshop.admin.feign;

import com.aheadshop.admin.domain.vo.RefundPageVO;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "aheadshop-order", path = "/inner/refund")
public interface RefundFeignClient {

    @GetMapping("/list")
    Result<PageResult<RefundPageVO>> listRefunds(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    @PutMapping("/audit/{refundId}")
    Result<Void> auditRefund(@PathVariable("refundId") Long refundId,
                             @RequestParam("approved") boolean approved,
                             @RequestParam(value = "remark", required = false) String remark);

    @GetMapping("/order/{orderNo}")
    Result<RefundPageVO> getRefundByOrderNo(@PathVariable("orderNo") String orderNo);
}
