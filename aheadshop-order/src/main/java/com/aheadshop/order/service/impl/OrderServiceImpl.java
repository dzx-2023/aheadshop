package com.aheadshop.order.service.impl;

import com.aheadshop.common.core.enums.OrderStatus;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.common.core.util.SnowflakeIdWorker;
import com.aheadshop.order.config.RabbitMQConfig;
import com.aheadshop.order.domain.dto.CreateOrderDTO;
import com.aheadshop.order.domain.dto.OrderQueryDTO;
import com.aheadshop.order.domain.dto.StockDTO;
import com.aheadshop.order.domain.po.Order;
import com.aheadshop.order.domain.po.OrderItem;
import com.aheadshop.order.domain.po.OrderLog;
import com.aheadshop.order.domain.vo.*;
import com.aheadshop.order.feign.CartFeignClient;
import com.aheadshop.order.feign.ProductFeignClient;
import com.aheadshop.order.feign.UserFeignClient;
import com.aheadshop.order.mapper.OrderItemMapper;
import com.aheadshop.order.mapper.OrderLogMapper;
import com.aheadshop.order.mapper.OrderMapper;
import com.aheadshop.order.service.IOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderLogMapper orderLogMapper;
    private final CartFeignClient cartFeignClient;
    private final ProductFeignClient productFeignClient;
    private final UserFeignClient userFeignClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public SubmitOrderVO createOrder(Long userId, CreateOrderDTO dto) {
        // 1. 获取购物车已勾选商品
        Result<List<CartItemVO>> cartResult = cartFeignClient.getCheckedItems(userId);
        if (cartResult == null || cartResult.getCode() != 200 || cartResult.getData() == null
                || cartResult.getData().isEmpty()) {
            throw new BusinessException(BusinessExceptionCode.CART_EMPTY, "购物车为空，请先添加商品");
        }
        List<CartItemVO> cartItems = cartResult.getData();

        // 2. 批量锁库存
        List<StockDTO> lockStockList = cartItems.stream()
                .map(item -> new StockDTO(item.getSkuId(), item.getQuantity()))
                .collect(Collectors.toList());
        Result<Void> lockResult = productFeignClient.lockStock(lockStockList);
        if (lockResult == null || lockResult.getCode() != 200) {
            throw new BusinessException(BusinessExceptionCode.STOCK_NOT_ENOUGH, "库存不足");
        }

        // 3. 获取地址快照
        Result<AddressSnapshotVO> addrResult = userFeignClient.getAddress(dto.getAddressId());
        if (addrResult == null || addrResult.getCode() != 200 || addrResult.getData() == null) {
            throw new BusinessException(BusinessExceptionCode.NOT_FOUND, "收货地址不存在");
        }
        AddressSnapshotVO address = addrResult.getData();
        String receiverAddress = address.getProvince() + address.getCity()
                + address.getDistrict() + address.getDetailAddress();

        // 4. 计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemVO item : cartItems) {
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 5. 生成订单号
        String orderNo = SnowflakeIdWorker.generateOrderNo();

        // 6. 保存订单（事务）
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.PENDING_PAY.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(receiverAddress);
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        // 7. 批量保存订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemVO item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setSkuId(item.getSkuId());
            orderItem.setSkuName(item.getSkuName());
            orderItem.setSkuImage(item.getImage());
            orderItem.setSpecs(item.getSpecs());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItems.add(orderItem);
        }
        orderItems.forEach(orderItemMapper::insert);

        // 8. 记录订单日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderNo(orderNo);
        orderLog.setOperator("system");
        orderLog.setStatusBefore(null);
        orderLog.setStatusAfter(OrderStatus.PENDING_PAY.getCode());
        orderLog.setNote("创建订单");
        orderLogMapper.insert(orderLog);

        // 9. 清除购物车已勾选商品
        try {
            cartFeignClient.clearCheckedItems(userId);
        } catch (Exception e) {
            log.warn("清除购物车已勾选商品失败: {}", e.getMessage());
        }

        // 10. 发送延时消息（30min 后检查是否支付）
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DELAY_EXCHANGE,
                    RabbitMQConfig.DELAY_ROUTING_KEY,
                    orderNo);
            log.info("订单 {} 延时消息已发送", orderNo);
        } catch (Exception e) {
            log.error("订单 {} 延时消息发送失败: {}", orderNo, e.getMessage());
        }

        return SubmitOrderVO.builder()
                .orderNo(orderNo)
                .totalAmount(totalAmount)
                .payAmount(totalAmount)
                .build();
    }

    @Override
    public PageResult<OrderPageVO> listOrders(Long userId, OrderQueryDTO dto) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(dto.getStatus() != null, Order::getStatus, dto.getStatus())
                .orderByDesc(Order::getCreateTime);

        Page<Order> page = orderMapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        List<OrderPageVO> records = page.getRecords().stream()
                .map(this::toPageVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), records);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        return toDetailVO(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY.getCode()) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "只有待支付订单才能取消");
        }

        // 更新状态
        Integer oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);

        // 释放库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
        if (!items.isEmpty()) {
            List<StockDTO> stockDTOs = items.stream()
                    .map(item -> new StockDTO(item.getSkuId(), item.getQuantity()))
                    .collect(Collectors.toList());
            productFeignClient.unlockStock(stockDTOs);
        }

        // 记录日志
        saveLog(orderNo, userId.toString(), oldStatus, OrderStatus.CANCELLED.getCode(), "用户取消订单");
    }

    @Override
    @Transactional
    public void confirmReceive(Long userId, String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != OrderStatus.SHIPPED.getCode()) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "只有已发货订单才能确认收货");
        }

        Integer oldStatus = order.getStatus();
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setReceiveTime(java.time.LocalDateTime.now());
        orderMapper.updateById(order);

        saveLog(orderNo, userId.toString(), oldStatus, OrderStatus.COMPLETED.getCode(), "用户确认收货");
    }

    @Override
    public OrderDetailVO getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        return toDetailVO(order);
    }

    @Override
    @Transactional
    public void paySuccess(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAY.getCode()) {
            log.warn("订单 {} 状态非待支付，忽略支付成功通知", orderNo);
            return;
        }

        Integer oldStatus = order.getStatus();
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPayTime(java.time.LocalDateTime.now());
        orderMapper.updateById(order);

        // 确认扣减库存（将锁定库存转为实际扣减）
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
        if (!items.isEmpty()) {
            List<StockDTO> stockDTOs = items.stream()
                    .map(item -> new StockDTO(item.getSkuId(), item.getQuantity()))
                    .collect(Collectors.toList());
            try {
                productFeignClient.confirmDeduct(stockDTOs);
            } catch (Exception e) {
                log.error("订单 {} 确认扣减库存失败: {}", orderNo, e.getMessage());
            }
        }

        saveLog(orderNo, "system", oldStatus, OrderStatus.PAID.getCode(), "支付成功");
        log.info("订单 {} 支付成功，状态已更新", orderNo);
    }

    // ========== 管理端接口 ==========

    @Override
    public PageResult<OrderPageVO> adminListOrders(Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);

        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<OrderPageVO> records = page.getRecords().stream()
                .map(this::toPageVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), records);
    }

    @Override
    @Transactional
    public void shipOrder(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(BusinessExceptionCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "只有已支付订单才能发货");
        }

        Integer oldStatus = order.getStatus();
        order.setStatus(OrderStatus.SHIPPED.getCode());
        order.setShipTime(java.time.LocalDateTime.now());
        orderMapper.updateById(order);

        saveLog(orderNo, "admin", oldStatus, OrderStatus.SHIPPED.getCode(), "管理员发货");
    }

    @Override
    public Long todayOrderCount() {
        java.time.LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        return orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, startOfDay));
    }

    @Override
    public BigDecimal todaySalesAmount() {
        java.time.LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, startOfDay)
                        .eq(Order::getStatus, OrderStatus.PAID.getCode())
                        .or()
                        .ge(Order::getCreateTime, startOfDay)
                        .eq(Order::getStatus, OrderStatus.SHIPPED.getCode())
                        .or()
                        .ge(Order::getCreateTime, startOfDay)
                        .eq(Order::getStatus, OrderStatus.COMPLETED.getCode()));
        return orders.stream()
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Long pendingShipCount() {
        return orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatus.PAID.getCode()));
    }

    @Override
    public List<Map<String, Object>> salesTrend() {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            java.time.LocalDateTime start = date.atStartOfDay();
            java.time.LocalDateTime end = date.plusDays(1).atStartOfDay();

            List<Order> orders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .ge(Order::getCreateTime, start)
                            .lt(Order::getCreateTime, end)
                            .in(Order::getStatus,
                                    OrderStatus.PAID.getCode(),
                                    OrderStatus.SHIPPED.getCode(),
                                    OrderStatus.COMPLETED.getCode()));

            BigDecimal amount = orders.stream()
                    .map(Order::getPayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> dayData = new java.util.HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("orderCount", orders.size());
            dayData.put("amount", amount);
            result.add(dayData);
        }

        return result;
    }

    private void saveLog(String orderNo, String operator, Integer from, Integer to, String note) {
        OrderLog logEntry = new OrderLog();
        logEntry.setOrderNo(orderNo);
        logEntry.setOperator(operator);
        logEntry.setStatusBefore(from);
        logEntry.setStatusAfter(to);
        logEntry.setNote(note);
        orderLogMapper.insert(logEntry);
    }

    private OrderPageVO toPageVO(Order order) {
        return OrderPageVO.builder()
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .createTime(order.getCreateTime())
                .build();
    }

    private OrderDetailVO toDetailVO(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, order.getOrderNo()));

        List<OrderItemVO> itemVOs = items.stream()
                .map(item -> OrderItemVO.builder()
                        .skuId(item.getSkuId())
                        .skuName(item.getSkuName())
                        .skuImage(item.getSkuImage())
                        .specs(item.getSpecs())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDetailVO.builder()
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .discountAmount(order.getDiscountAmount())
                .freightAmount(order.getFreightAmount())
                .status(order.getStatus())
                .payType(order.getPayType())
                .payTime(order.getPayTime())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .items(itemVOs)
                .build();
    }
}
