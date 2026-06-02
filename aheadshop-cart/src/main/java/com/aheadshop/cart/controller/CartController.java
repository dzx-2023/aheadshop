package com.aheadshop.cart.controller;

import com.aheadshop.cart.domain.CartItem;
import com.aheadshop.cart.service.CartService;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "购物车接口")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public Result<Void> add(@RequestHeader("X-User-Id") Long userId,
                            @RequestBody Map<String, Object> body) {
        Long skuId = Long.valueOf(body.get("skuId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.addItem(userId, skuId, quantity);
        return Result.success(null);
    }

    @Operation(summary = "购物车列表")
    @GetMapping("/list")
    public Result<List<CartItem>> list(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(cartService.list(userId));
    }

    @Operation(summary = "修改数量")
    @PutMapping("/update")
    public Result<Void> update(@RequestHeader("X-User-Id") Long userId,
                               @RequestBody Map<String, Object> body) {
        Long skuId = Long.valueOf(body.get("skuId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.updateQuantity(userId, skuId, quantity);
        return Result.success(null);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/delete/{skuId}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId,
                               @PathVariable("skuId") Long skuId) {
        cartService.deleteItem(userId, skuId);
        return Result.success(null);
    }

    @Operation(summary = "购物车商品数量")
    @GetMapping("/count")
    public Result<Long> count(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(cartService.count(userId));
    }

    @Operation(summary = "勾选/取消勾选")
    @PutMapping("/check/{skuId}")
    public Result<Void> check(@RequestHeader("X-User-Id") Long userId,
                              @PathVariable("skuId") Long skuId,
                              @RequestParam Integer checked) {
        cartService.checkItem(userId, skuId, checked);
        return Result.success(null);
    }
}
