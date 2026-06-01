package com.aheadshop.pay.feign;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.pay.domain.vo.OrderInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "aheadshop-order", path = "/inner/order")
public interface OrderFeignClient {

    @GetMapping("/{orderNo}")
    Result<OrderInfoVO> getOrder(@PathVariable("orderNo") String orderNo);

    @PutMapping("/pay-success/{orderNo}")
    Result<Void> paySuccess(@PathVariable("orderNo") String orderNo);
}
