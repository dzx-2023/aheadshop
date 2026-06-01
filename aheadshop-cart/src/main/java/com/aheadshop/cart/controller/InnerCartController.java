package com.aheadshop.cart.controller;

import com.aheadshop.cart.domain.CartItem;
import com.aheadshop.cart.service.CartService;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inner/cart")
@RequiredArgsConstructor
@Tag(name = "购物车内部接口（供订单服务调用）")
public class InnerCartController {

    private final CartService cartService;

    @Operation(summary = "获取已勾选商品")
    @GetMapping("/checked/{userId}")
    public Result<List<CartItem>> getCheckedItems(@PathVariable Long userId) {
        return Result.success(cartService.getCheckedItems(userId));
    }

    @Operation(summary = "清除已勾选商品")
    @DeleteMapping("/checked/{userId}")
    public Result<Void> clearCheckedItems(@PathVariable Long userId) {
        cartService.clearCheckedItems(userId);
        return Result.success(null);
    }
}
