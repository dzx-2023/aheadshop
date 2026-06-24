package com.aheadshop.distribution.consumer;

import com.aheadshop.distribution.config.DistributionMQConfig;
import com.aheadshop.distribution.domain.dto.OrderPaidMessage;
import com.aheadshop.distribution.domain.dto.OrderRefundedMessage;
import com.aheadshop.distribution.service.ICommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ICommissionService commissionService;

    @RabbitListener(queues = DistributionMQConfig.PAID_QUEUE)
    public void handleOrderPaid(OrderPaidMessage msg) {
        log.info("收到订单支付消息: orderNo={}, userId={}, payAmount={}", msg.getOrderNo(), msg.getUserId(), msg.getPayAmount());
        try {
            commissionService.calculateCommission(msg.getOrderNo(), msg.getUserId(), msg.getPayAmount());
        } catch (Exception e) {
            log.error("佣金计算失败: orderNo={}", msg.getOrderNo(), e);
        }
    }

    @RabbitListener(queues = DistributionMQConfig.REFUNDED_QUEUE)
    public void handleOrderRefunded(OrderRefundedMessage msg) {
        log.info("收到订单退款消息: orderNo={}", msg.getOrderNo());
        try {
            commissionService.cancelCommission(msg.getOrderNo());
        } catch (Exception e) {
            log.error("佣金取消失败: orderNo={}", msg.getOrderNo(), e);
        }
    }
}
