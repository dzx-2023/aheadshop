package com.aheadshop.user.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.user.domain.vo.*;
import com.aheadshop.user.service.ISignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/sign")
@RequiredArgsConstructor
@Tag(name = "签到积分接口")
public class SignController {

    private final ISignService signService;

    @Operation(summary = "每日签到")
    @PostMapping("/check-in")
    public Result<SignResultVO> checkIn(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(signService.checkIn(userId));
    }

    @Operation(summary = "补签", description = "补签指定日期，消耗积分")
    @PostMapping("/retro/{date}")
    public Result<Void> retroSign(@RequestHeader("X-User-Id") Long userId,
                                  @PathVariable("date") String date) {
        signService.retroSign(userId, date);
        return Result.success(null);
    }

    @Operation(summary = "当月签到状态", description = "返回日历所需的签到日期列表")
    @GetMapping("/status")
    public Result<SignStatusVO> getMonthStatus(@RequestHeader("X-User-Id") Long userId,
                                               @RequestParam(required = false) Integer year,
                                               @RequestParam(required = false) Integer month) {
        return Result.success(signService.getMonthStatus(userId, year, month));
    }

    @Operation(summary = "连续签到天数")
    @GetMapping("/streak")
    public Result<Integer> getStreak(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(signService.getStreak(userId));
    }

    @Operation(summary = "我的积分信息")
    @GetMapping("/points")
    public Result<PointsVO> getPoints(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(signService.getPoints(userId));
    }

    @Operation(summary = "积分流水", description = "积分收支记录(分页)")
    @GetMapping("/log")
    public Result<PageResult<PointsLogVO>> getPointLog(@RequestHeader("X-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(signService.getPointLog(userId, pageNum, pageSize));
    }

    @Operation(summary = "积分排行榜", description = "type 可选 total(总排行) 或 month(月排行)")
    @GetMapping("/leaderboard")
    public Result<List<LeaderboardItemVO>> getLeaderboard(@RequestParam(defaultValue = "total") String type) {
        return Result.success(signService.getLeaderboard(type));
    }
}
