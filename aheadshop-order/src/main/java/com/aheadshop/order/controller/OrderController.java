package com.aheadshop.order.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.order.domain.dto.CreateOrderDTO;
import com.aheadshop.order.domain.dto.OrderQueryDTO;
import com.aheadshop.order.domain.vo.OrderDetailVO;
import com.aheadshop.order.domain.vo.OrderPageVO;
import com.aheadshop.order.domain.vo.SubmitOrderVO;
import com.aheadshop.order.service.IOrderService;
import com.aheadshop.order.domain.vo.RefundPageVO;
import com.aheadshop.order.service.IRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "订单接口")
public class OrderController {

    private final IOrderService orderService;
    private final IRefundService refundService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<SubmitOrderVO> create(@RequestHeader("X-User-Id") Long userId,
                                        @Valid @RequestBody CreateOrderDTO dto) {
        return Result.success(orderService.createOrder(userId, dto));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/list")
    public Result<PageResult<OrderPageVO>> list(@RequestHeader("X-User-Id") Long userId,
                                                OrderQueryDTO dto) {
        return Result.success(orderService.listOrders(userId, dto));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/detail/{orderNo}")
    public Result<OrderDetailVO> detail(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getOrderDetail(userId, orderNo));
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{orderNo}")
    public Result<Void> cancel(@RequestHeader("X-User-Id") Long userId,
                               @PathVariable("orderNo") String orderNo) {
        orderService.cancelOrder(userId, orderNo);
        return Result.success(null);
    }

    @Operation(summary = "确认收货")
    @PutMapping("/confirm/{orderNo}")
    public Result<Void> confirm(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable("orderNo") String orderNo) {
        orderService.confirmReceive(userId, orderNo);
        return Result.success(null);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{orderNo}")
    public Result<Void> refund(@RequestHeader("X-User-Id") Long userId,
                               @PathVariable("orderNo") String orderNo,
                               @RequestParam(value = "refundType", defaultValue = "1") Integer refundType,
                               @RequestParam(value = "reason", required = false) String reason) {
        refundService.createRefund(userId, orderNo, refundType, reason);
        return Result.success(null);
    }

    @Operation(summary = "查询退款信息")
    @GetMapping("/refund/{orderNo}")
    public Result<RefundPageVO> getRefund(@PathVariable("orderNo") String orderNo) {
        return Result.success(refundService.getRefundByOrderNo(orderNo));
    }

    @Operation(summary = "我的退款列表")
    @GetMapping("/refund/list")
    public Result<PageResult<RefundPageVO>> refundList(@RequestHeader("X-User-Id") Long userId,
                                                       @RequestParam(value = "status", required = false) Integer status,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(refundService.listUserRefunds(userId, status, pageNum, pageSize));
    }
}
