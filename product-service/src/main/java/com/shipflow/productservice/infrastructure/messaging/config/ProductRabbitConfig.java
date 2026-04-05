package com.shipflow.productservice.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shipflow.config.message.RabbitMqConfig;

@Configuration
public class ProductRabbitConfig {

    private static final String ROUTING_ORDER_CREATION_STARTED = "order.creation.started";

    public static final String QUEUE_PRODUCT_ORDER_CREATION_STARTED = "product.order.creation.started";
    public static final String QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ = QUEUE_PRODUCT_ORDER_CREATION_STARTED + ".dlq";

    public static final String ROUTING_ORDER_CANCELED = "order.canceled";
    public static final String ROUTING_ORDER_CREATION_FAILED = "order.creation.failed";

    public static final String QUEUE_PRODUCT_STOCK_RESTORED = "product.stock.restored";
    public static final String QUEUE_PRODUCT_STOCK_RESTORED_DLQ = "product.stock.restored.dlq";



    @Bean
    public Queue queueProductOrderCreationStarted() {
        return RabbitMqConfig.durableQueue(QUEUE_PRODUCT_ORDER_CREATION_STARTED);
    }

    @Bean
    public Queue queueProductOrderCreationStartedDlq() {
        return RabbitMqConfig.dlqQueue(QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ);
    }

    @Bean
    public Queue queueProductStockRestored() {
        return RabbitMqConfig.durableQueue(QUEUE_PRODUCT_STOCK_RESTORED);
    }

    @Bean
    public Queue queueProductStockRestoredDlq() {
        return RabbitMqConfig.dlqQueue(QUEUE_PRODUCT_STOCK_RESTORED_DLQ);
    }

    @Bean
    public Binding bindProductOrderCreationStarted(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueProductOrderCreationStarted())
                .to(sagaExchange)
                .with(ROUTING_ORDER_CREATION_STARTED);
    }

    @Bean
    public Binding bindProductOrderCreationStartedDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueProductOrderCreationStartedDlq())
                .to(sagaDlx)
                .with(QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ);
    }

    @Bean
    public Binding bindProductStockRestored(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueProductStockRestored())
            .to(sagaExchange)
            .with(ROUTING_ORDER_CANCELED);
    }

    @Bean
    public Binding bindProductStockRestoredFailed(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueProductStockRestored())
            .to(sagaExchange)
            .with(ROUTING_ORDER_CREATION_FAILED);
    }

    @Bean
    public Binding bindProductStockRestoredDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueProductStockRestoredDlq())
            .to(sagaDlx)
            .with(QUEUE_PRODUCT_STOCK_RESTORED_DLQ);
    }
}
