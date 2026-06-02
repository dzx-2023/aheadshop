package com.aheadshop.admin.feign;

import com.aheadshop.admin.domain.vo.OrderDetailVO;
import com.aheadshop.admin.domain.vo.OrderPageVO;
import com.aheadshop.admin.domain.vo.RefundPageVO;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "aheadshop-order", path = "/inner/order")
public interface OrderFeignClient {

    @GetMapping("/{orderNo}")
    Result<OrderDetailVO> getOrder(@PathVariable("orderNo") String orderNo);

    @GetMapping("/list")
    Result<PageResult<OrderPageVO>> listOrders(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize);

    @PutMapping("/ship/{orderNo}")
    Result<Void> shipOrder(@PathVariable("orderNo") String orderNo,
                           @RequestParam(value = "trackingNumber", required = false) String trackingNumber);

    @GetMapping("/today-count")
    Result<Long> todayOrderCount();

    @GetMapping("/today-amount")
    Result<java.math.BigDecimal> todaySalesAmount();

    @GetMapping("/pending-ship-count")
    Result<Long> pendingShipCount();

    @GetMapping("/sales-trend")
    Result<java.util.List<java.util.Map<String, Object>>> salesTrend();
}
