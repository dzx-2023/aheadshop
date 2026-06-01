package com.aheadshop.order.listener;

import com.aheadshop.common.core.enums.OrderStatus;
import com.aheadshop.order.config.RabbitMQConfig;
import com.aheadshop.order.domain.dto.StockDTO;
import com.aheadshop.order.domain.po.Order;
import com.aheadshop.order.domain.po.OrderItem;
import com.aheadshop.order.feign.ProductFeignClient;
import com.aheadshop.order.mapper.OrderItemMapper;
import com.aheadshop.order.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutListener {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductFeignClient productFeignClient;

    @RabbitListener(queues = RabbitMQConfig.TIMEOUT_QUEUE)
    @Transactional
    public void handleTimeout(String orderNo) {
        log.info("订单超时检查: {}", orderNo);

        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));

        if (order == null || order.getStatus() != OrderStatus.PENDING_PAY.getCode()) {
            log.info("订单 {} 无需取消（不存在或状态非待支付）", orderNo);
            return;
        }

        // 更新状态为已取消
        order.setStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);

        // 解锁库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
        if (!items.isEmpty()) {
            List<StockDTO> stockDTOs = items.stream()
                    .map(item -> new StockDTO(item.getSkuId(), item.getQuantity()))
                    .collect(Collectors.toList());
            try {
                productFeignClient.unlockStock(stockDTOs);
                log.info("订单 {} 库存已释放", orderNo);
            } catch (Exception e) {
                log.error("订单 {} 库存释放失败: {}", orderNo, e.getMessage());
            }
        }

        log.info("订单 {} 已超时取消", orderNo);
    }
}
