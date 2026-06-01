package com.aheadshop.order.feign;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.order.domain.vo.CartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "aheadshop-cart", path = "/inner/cart")
public interface CartFeignClient {

    @GetMapping("/checked/{userId}")
    Result<List<CartItemVO>> getCheckedItems(@PathVariable("userId") Long userId);

    @DeleteMapping("/checked/{userId}")
    Result<Void> clearCheckedItems(@PathVariable("userId") Long userId);
}
