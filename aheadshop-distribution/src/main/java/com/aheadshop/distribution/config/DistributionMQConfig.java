package com.aheadshop.distribution.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributionMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAID_QUEUE = "distribution.order.paid";
    public static final String PAID_ROUTING = "order.paid";
    public static final String REFUNDED_QUEUE = "distribution.order.refunded";
    public static final String REFUNDED_ROUTING = "order.refunded";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Queue paidQueue() {
        return QueueBuilder.durable(PAID_QUEUE).build();
    }

    @Bean
    public Queue refundedQueue() {
        return QueueBuilder.durable(REFUNDED_QUEUE).build();
    }

    @Bean
    public Binding paidBinding(Queue paidQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(paidQueue).to(orderExchange).with(PAID_ROUTING);
    }

    @Bean
    public Binding refundedBinding(Queue refundedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(refundedQueue).to(orderExchange).with(REFUNDED_ROUTING);
    }
}
