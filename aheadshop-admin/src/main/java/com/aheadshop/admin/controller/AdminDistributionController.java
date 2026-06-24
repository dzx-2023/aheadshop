package com.aheadshop.admin.controller;

import com.aheadshop.admin.feign.DistributionFeignClient;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/distribution")
@RequiredArgsConstructor
@Tag(name = "管理端-分销商管理")
public class AdminDistributionController {

    private final DistributionFeignClient distributionFeignClient;

    // ========== 分销商管理 ==========

    @Operation(summary = "分销商列表")
    @GetMapping("/list")
    public Result<?> listDistributors(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return distributionFeignClient.listDistributors(status, keyword, pageNum, pageSize);
    }

    @Operation(summary = "启用/禁用分销商")
    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable("userId") Long userId,
                                     @RequestParam("status") Integer status) {
        return distributionFeignClient.updateStatus(userId, status);
    }

    // ========== 提现管理 ==========

    @Operation(summary = "提现记录列表")
    @GetMapping("/withdraw/list")
    public Result<?> listWithdrawals(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return distributionFeignClient.listWithdrawals(status, pageNum, pageSize);
    }

    @Operation(summary = "审核提现")
    @PostMapping("/withdraw/audit")
    public Result<Void> auditWithdraw(@RequestBody Map<String, Object> params) {
        return distributionFeignClient.auditWithdraw(params);
    }

    @Operation(summary = "确认打款")
    @PostMapping("/withdraw/pay/{id}")
    public Result<Void> confirmPaid(@PathVariable("id") Long id) {
        return distributionFeignClient.confirmPaid(id);
    }

    // ========== 佣金查看 ==========

    @Operation(summary = "佣金记录列表")
    @GetMapping("/commission/list")
    public Result<?> listCommissions(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return distributionFeignClient.listCommissions(status, pageNum, pageSize);
    }
}
