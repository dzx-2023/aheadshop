package com.aheadshop.distribution.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.distribution.domain.dto.WithdrawAuditDTO;
import com.aheadshop.distribution.domain.po.CommissionRecord;
import com.aheadshop.distribution.domain.po.Distributor;
import com.aheadshop.distribution.domain.po.WithdrawalRecord;
import com.aheadshop.distribution.service.ICommissionService;
import com.aheadshop.distribution.service.IDistributionService;
import com.aheadshop.distribution.service.IWithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/distribution")
@RequiredArgsConstructor
@Tag(name = "分销内部接口（供其他微服务调用）")
public class InnerDistributionController {

    private final IDistributionService distributionService;
    private final ICommissionService commissionService;
    private final IWithdrawalService withdrawalService;

    // ========== 基础接口 ==========

    @Operation(summary = "生成邀请码（注册时调用）")
    @PostMapping("/generate-code/{userId}")
    public Result<String> generateInviteCode(@PathVariable("userId") Long userId) {
        String code = distributionService.generateInviteCode(userId);
        return Result.success(code);
    }

    @Operation(summary = "绑定推荐关系（通过邀请码），返回推荐人userId")
    @PostMapping("/bind-referral")
    public Result<Long> bindReferral(@RequestParam("userId") Long userId,
                                     @RequestParam("inviteCode") String inviteCode) {
        Long referrerUserId = distributionService.bindReferralByInviteCode(userId, inviteCode);
        return Result.success(referrerUserId);
    }

    @Operation(summary = "获取分销商信息")
    @GetMapping("/distributor/{userId}")
    public Result<Distributor> getDistributor(@PathVariable("userId") Long userId) {
        return Result.success(distributionService.getByUserId(userId));
    }

    @Operation(summary = "判断用户是否为分销商")
    @GetMapping("/exists/{userId}")
    public Result<Boolean> exists(@PathVariable("userId") Long userId) {
        return Result.success(distributionService.existsByUserId(userId));
    }

    // ========== 管理端-分销商管理 ==========

    @Operation(summary = "管理端-分销商列表")
    @GetMapping("/admin/list")
    public Result<PageResult<Distributor>> adminListDistributors(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(distributionService.adminList(status, keyword, pageNum, pageSize));
    }

    @Operation(summary = "管理端-启用/禁用分销商")
    @PutMapping("/admin/{userId}/status")
    public Result<Void> adminUpdateStatus(@PathVariable("userId") Long userId,
                                          @RequestParam("status") Integer status) {
        distributionService.adminUpdateStatus(userId, status);
        return Result.success(null);
    }

    // ========== 管理端-提现管理 ==========

    @Operation(summary = "管理端-审核提现")
    @PostMapping("/withdraw/audit")
    public Result<Void> auditWithdraw(@RequestBody WithdrawAuditDTO dto) {
        withdrawalService.audit(dto);
        return Result.success(null);
    }

    @Operation(summary = "管理端-提现记录列表")
    @GetMapping("/withdraw/list")
    public Result<PageResult<WithdrawalRecord>> listWithdrawals(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(withdrawalService.getList(null, status, pageNum, pageSize));
    }

    // ========== 管理端-佣金查看 ==========

    @Operation(summary = "管理端-确认打款")
    @PostMapping("/withdraw/pay/{id}")
    public Result<Void> confirmPaid(@PathVariable("id") Long id) {
        withdrawalService.confirmPaid(id);
        return Result.success(null);
    }

    @Operation(summary = "管理端-佣金记录列表")
    @GetMapping("/commission/list")
    public Result<PageResult<CommissionRecord>> listCommissions(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(commissionService.getList(null, status, pageNum, pageSize));
    }
}
