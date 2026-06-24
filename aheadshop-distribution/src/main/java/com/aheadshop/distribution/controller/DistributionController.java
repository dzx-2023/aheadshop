package com.aheadshop.distribution.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.distribution.domain.dto.WithdrawApplyDTO;
import com.aheadshop.distribution.domain.po.CommissionRecord;
import com.aheadshop.distribution.domain.po.WithdrawalRecord;
import com.aheadshop.distribution.domain.vo.CommissionSummaryVO;
import com.aheadshop.distribution.domain.vo.DistributionInfoVO;
import com.aheadshop.distribution.domain.vo.ReferrerInfoVO;
import com.aheadshop.distribution.domain.vo.TeamMemberVO;
import com.aheadshop.distribution.service.ICommissionService;
import com.aheadshop.distribution.service.IDistributionService;
import com.aheadshop.distribution.service.IWithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/distribution")
@RequiredArgsConstructor
@Tag(name = "分销商用户端接口")
public class DistributionController {

    private final IDistributionService distributionService;
    private final ICommissionService commissionService;
    private final IWithdrawalService withdrawalService;

    // ========== 分销中心 ==========

    @Operation(summary = "获取分销中心信息")
    @GetMapping("/info")
    public Result<DistributionInfoVO> getInfo(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(distributionService.getInfo(userId));
    }

    @Operation(summary = "获取团队成员列表（分页）")
    @GetMapping("/team")
    public Result<List<TeamMemberVO>> getTeamList(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(distributionService.getTeamList(userId));
    }

    @Operation(summary = "获取我的推荐人信息")
    @GetMapping("/referrer")
    public Result<ReferrerInfoVO> getReferrerInfo(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(distributionService.getReferrerInfo(userId));
    }

    @Operation(summary = "解除推荐关系")
    @PostMapping("/referrer/unbind")
    public Result<Void> unbindReferral(@RequestHeader("X-User-Id") Long userId) {
        distributionService.unbindReferral(userId);
        return Result.success(null);
    }

    // ========== 佣金 ==========

    @Operation(summary = "获取佣金汇总")
    @GetMapping("/commission/summary")
    public Result<CommissionSummaryVO> getCommissionSummary(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(commissionService.getSummary(userId));
    }

    @Operation(summary = "佣金明细列表（分页+状态筛选）")
    @GetMapping("/commission/list")
    public Result<PageResult<CommissionRecord>> getCommissionList(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(commissionService.getList(userId, status, pageNum, pageSize));
    }

    // ========== 提现 ==========

    @Operation(summary = "申请提现")
    @PostMapping("/withdraw/apply")
    public Result<Void> applyWithdraw(@RequestHeader("X-User-Id") Long userId,
                                      @Valid @RequestBody WithdrawApplyDTO dto) {
        withdrawalService.apply(userId, dto);
        return Result.success(null);
    }

    @Operation(summary = "提现记录列表")
    @GetMapping("/withdraw/list")
    public Result<PageResult<WithdrawalRecord>> getWithdrawList(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(withdrawalService.getList(userId, null, pageNum, pageSize));
    }
}
