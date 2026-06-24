package com.aheadshop.admin.feign;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "aheadshop-distribution", path = "/inner/distribution")
public interface DistributionFeignClient {

    @GetMapping("/admin/list")
    Result<?> listDistributors(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize);

    @PutMapping("/admin/{userId}/status")
    Result<Void> updateStatus(@PathVariable("userId") Long userId,
                              @RequestParam("status") Integer status);

    @GetMapping("/withdraw/list")
    Result<?> listWithdrawals(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize);

    @PostMapping("/withdraw/audit")
    Result<Void> auditWithdraw(@RequestBody Map<String, Object> params);

    @PostMapping("/withdraw/pay/{id}")
    Result<Void> confirmPaid(@PathVariable("id") Long id);

    @GetMapping("/commission/list")
    Result<?> listCommissions(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize);
}
