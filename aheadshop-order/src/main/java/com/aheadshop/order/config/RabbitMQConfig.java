package com.aheadshop.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // ========== 延时队列（订单超时取消） ==========

    public static final String DELAY_EXCHANGE = "order.delay.exchange";
    public static final String DELAY_QUEUE = "order.delay.queue";
    public static final String DELAY_ROUTING_KEY = "order.delay";

    public static final String TIMEOUT_EXCHANGE = "order.timeout.exchange";
    public static final String TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String TIMEOUT_ROUTING_KEY = "order.timeout";

    private static final long ORDER_TIMEOUT_MS = 30 * 60 * 1000L; // 30 分钟

    /** 延时交换机 */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE, true, false);
    }

    /** 延时队列：TTL 后消息进入死信交换机 */
    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", TIMEOUT_EXCHANGE);
        args.put("x-dead-letter-routing-key", TIMEOUT_ROUTING_KEY);
        args.put("x-message-ttl", ORDER_TIMEOUT_MS);
        return new Queue(DELAY_QUEUE, true, false, false, args);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_ROUTING_KEY);
    }

    /** 超时交换机（死信） */
    @Bean
    public DirectExchange timeoutExchange() {
        return new DirectExchange(TIMEOUT_EXCHANGE, true, false);
    }

    /** 超时队列（消费者监听） */
    @Bean
    public Queue timeoutQueue() {
        return new Queue(TIMEOUT_QUEUE, true);
    }

    @Bean
    public Binding timeoutBinding() {
        return BindingBuilder.bind(timeoutQueue()).to(timeoutExchange()).with(TIMEOUT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true);
        return template;
    }
}
