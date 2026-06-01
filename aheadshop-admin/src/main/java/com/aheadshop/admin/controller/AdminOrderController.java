package com.aheadshop.admin.controller;

import com.aheadshop.admin.domain.vo.OrderPageVO;
import com.aheadshop.admin.feign.OrderFeignClient;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
@Tag(name = "管理端-订单管理")
public class AdminOrderController {

    private final OrderFeignClient orderFeignClient;

    @Operation(summary = "订单列表")
    @GetMapping("/list")
    public Result<PageResult<OrderPageVO>> list(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderFeignClient.listOrders(status, pageNum, pageSize);
    }

    @Operation(summary = "发货")
    @PutMapping("/ship/{orderNo}")
    public Result<Void> ship(@PathVariable("orderNo") String orderNo) {
        return orderFeignClient.shipOrder(orderNo);
    }
}
