package com.aheadshop.order.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.order.domain.vo.OrderDetailVO;
import com.aheadshop.order.domain.vo.OrderPageVO;
import com.aheadshop.order.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inner/order")
@RequiredArgsConstructor
@Tag(name = "订单内部接口（供支付/管理服务调用）")
public class InnerOrderController {

    private final IOrderService orderService;

    @Operation(summary = "根据订单号查询订单")
    @GetMapping("/{orderNo}")
    public Result<OrderDetailVO> getOrder(@PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getOrderByOrderNo(orderNo));
    }

    @Operation(summary = "支付成功更新状态")
    @PutMapping("/pay-success/{orderNo}")
    public Result<Void> paySuccess(@PathVariable("orderNo") String orderNo) {
        orderService.paySuccess(orderNo);
        return Result.success(null);
    }

    @Operation(summary = "退款成功更新状态")
    @PutMapping("/refund-success/{orderNo}")
    public Result<Void> refundSuccess(@PathVariable("orderNo") String orderNo) {
        orderService.refundSuccess(orderNo);
        return Result.success(null);
    }

    @Operation(summary = "管理端-订单列表（全量）")
    @GetMapping("/list")
    public Result<PageResult<OrderPageVO>> adminList(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.adminListOrders(status, pageNum, pageSize));
    }

    @Operation(summary = "管理端-发货")
    @PutMapping("/ship/{orderNo}")
    public Result<Void> ship(@PathVariable("orderNo") String orderNo,
                             @RequestParam(value = "trackingNumber", required = false) String trackingNumber) {
        orderService.shipOrder(orderNo, trackingNumber);
        return Result.success(null);
    }

    @Operation(summary = "今日订单数")
    @GetMapping("/today-count")
    public Result<Long> todayOrderCount() {
        return Result.success(orderService.todayOrderCount());
    }

    @Operation(summary = "今日销售额")
    @GetMapping("/today-amount")
    public Result<BigDecimal> todaySalesAmount() {
        return Result.success(orderService.todaySalesAmount());
    }

    @Operation(summary = "待发货订单数")
    @GetMapping("/pending-ship-count")
    public Result<Long> pendingShipCount() {
        return Result.success(orderService.pendingShipCount());
    }

    @Operation(summary = "近7天销售趋势")
    @GetMapping("/sales-trend")
    public Result<List<Map<String, Object>>> salesTrend() {
        return Result.success(orderService.salesTrend());
    }
}
