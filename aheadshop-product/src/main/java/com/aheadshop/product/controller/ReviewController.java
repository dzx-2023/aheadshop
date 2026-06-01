package com.aheadshop.product.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.product.domain.dto.ReviewDTO;
import com.aheadshop.product.domain.vo.ReviewVO;
import com.aheadshop.product.service.IReviewService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/review")
@RequiredArgsConstructor
@Tag(name = "商品评价接口")
public class ReviewController {

    private final IReviewService reviewService;

    @Operation(summary = "发表评价")
    @PostMapping
    public Result<Void> add(@RequestHeader("X-User-Id") Long userId,
                            @Valid @RequestBody ReviewDTO dto) {
        reviewService.addReview(userId, dto);
        return Result.success(null);
    }

    @Operation(summary = "SPU 评价列表")
    @GetMapping("/list/{spuId}")
    public Result<IPage<ReviewVO>> list(
            @PathVariable Long spuId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(reviewService.listBySpuId(spuId, pageNum, pageSize));
    }

    @Operation(summary = "SPU 平均评分")
    @GetMapping("/avg-score/{spuId}")
    public Result<Double> avgScore(@PathVariable Long spuId) {
        return Result.success(reviewService.getAvgScore(spuId));
    }

    @Operation(summary = "我的评价列表")
    @GetMapping("/my")
    public Result<IPage<ReviewVO>> myReviews(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(reviewService.listByUserId(userId, pageNum, pageSize));
    }

    @Operation(summary = "检查订单是否已评价")
    @GetMapping("/check/{orderId}")
    public Result<Boolean> checkReviewed(@PathVariable Long orderId) {
        return Result.success(reviewService.hasReviewed(orderId));
    }
}
