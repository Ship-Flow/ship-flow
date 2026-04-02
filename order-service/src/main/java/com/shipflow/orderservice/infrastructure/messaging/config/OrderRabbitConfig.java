package com.shipflow.orderservice.infrastructure.messaging.config;

import com.shipflow.common.messaging.event.EventType;
import com.shipflow.config.message.RabbitMqConfig;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitConfig {

    // ── Queue 이름 상수 ────────────────────────────
    public static final String QUEUE_ORDER_STOCK_DECREASED           = "order.product.stock.decreased";
    public static final String QUEUE_ORDER_STOCK_DECREASED_FAILED    = "order.product.stock.decreased.failed";
    public static final String QUEUE_ORDER_STOCK_RESTORED            = "order.product.stock.restored";
    public static final String QUEUE_ORDER_SHIPMENT_CREATED          = "order.shipment.created";
    public static final String QUEUE_ORDER_SHIPMENT_CREATION_FAILED  = "order.shipment.creation.failed";
    public static final String QUEUE_ORDER_SHIPMENT_COMPLETED        = "order.shipment.completed";

    // ── DLQ 이름 상수 ──────────────────────────────
    public static final String QUEUE_ORDER_STOCK_DECREASED_DLQ           = QUEUE_ORDER_STOCK_DECREASED          + ".dlq";
    public static final String QUEUE_ORDER_STOCK_DECREASED_FAILED_DLQ   = QUEUE_ORDER_STOCK_DECREASED_FAILED   + ".dlq";
    public static final String QUEUE_ORDER_STOCK_RESTORED_DLQ           = QUEUE_ORDER_STOCK_RESTORED            + ".dlq";
    public static final String QUEUE_ORDER_SHIPMENT_CREATED_DLQ         = QUEUE_ORDER_SHIPMENT_CREATED          + ".dlq";
    public static final String QUEUE_ORDER_SHIPMENT_CREATION_FAILED_DLQ = QUEUE_ORDER_SHIPMENT_CREATION_FAILED  + ".dlq";
    public static final String QUEUE_ORDER_SHIPMENT_COMPLETED_DLQ       = QUEUE_ORDER_SHIPMENT_COMPLETED        + ".dlq";

    // ── Queue Bean ─────────────────────────────────
    @Bean public Queue queueOrderStockDecreased()          { return RabbitMqConfig.durableQueue(QUEUE_ORDER_STOCK_DECREASED); }
    @Bean public Queue queueOrderStockDecreasedFailed()    { return RabbitMqConfig.durableQueue(QUEUE_ORDER_STOCK_DECREASED_FAILED); }
    @Bean public Queue queueOrderStockRestored()           { return RabbitMqConfig.durableQueue(QUEUE_ORDER_STOCK_RESTORED); }
    @Bean public Queue queueOrderShipmentCreated()         { return RabbitMqConfig.durableQueue(QUEUE_ORDER_SHIPMENT_CREATED); }
    @Bean public Queue queueOrderShipmentCreationFailed()  { return RabbitMqConfig.durableQueue(QUEUE_ORDER_SHIPMENT_CREATION_FAILED); }
    @Bean public Queue queueOrderShipmentCompleted()       { return RabbitMqConfig.durableQueue(QUEUE_ORDER_SHIPMENT_COMPLETED); }

    // ── DLQ Bean ──────────────────────────────────
    @Bean public Queue queueOrderStockDecreasedDlq()         { return RabbitMqConfig.dlqQueue(QUEUE_ORDER_STOCK_DECREASED_DLQ); }
    @Bean public Queue queueOrderStockDecreasedFailedDlq()   { return RabbitMqConfig.dlqQueue(QUEUE_ORDER_STOCK_DECREASED_FAILED_DLQ); }
    @Bean public Queue queueOrderStockRestoredDlq()          { return RabbitMqConfig.dlqQueue(QUEUE_ORDER_STOCK_RESTORED_DLQ); }
    @Bean public Queue queueOrderShipmentCreatedDlq()        { return RabbitMqConfig.dlqQueue(QUEUE_ORDER_SHIPMENT_CREATED_DLQ); }
    @Bean public Queue queueOrderShipmentCreationFailedDlq() { return RabbitMqConfig.dlqQueue(QUEUE_ORDER_SHIPMENT_CREATION_FAILED_DLQ); }
    @Bean public Queue queueOrderShipmentCompletedDlq()      { return RabbitMqConfig.dlqQueue(QUEUE_ORDER_SHIPMENT_COMPLETED_DLQ); }

    // ── Binding Bean ───────────────────────────────
    @Bean public Binding bindOrderStockDecreased(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueOrderStockDecreased()).to(sagaExchange).with(EventType.PRODUCT_STOCK_DECREASED);
    }
    @Bean public Binding bindOrderStockDecreasedFailed(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueOrderStockDecreasedFailed()).to(sagaExchange).with(EventType.PRODUCT_STOCK_DECREASED_FAILED);
    }
    @Bean public Binding bindOrderStockRestored(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueOrderStockRestored()).to(sagaExchange).with(EventType.PRODUCT_STOCK_RESTORED);
    }
    @Bean public Binding bindOrderShipmentCreated(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueOrderShipmentCreated()).to(sagaExchange).with(EventType.SHIPMENT_CREATED);
    }
    @Bean public Binding bindOrderShipmentCreationFailed(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueOrderShipmentCreationFailed()).to(sagaExchange).with(EventType.SHIPMENT_CREATION_FAILED);
    }
    @Bean public Binding bindOrderShipmentCompleted(TopicExchange sagaExchange) {
        return BindingBuilder.bind(queueOrderShipmentCompleted()).to(sagaExchange).with(EventType.SHIPMENT_COMPLETED);
    }

    // ── DLQ Binding Bean ──────────────────────────
    @Bean public Binding bindOrderStockDecreasedDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueOrderStockDecreasedDlq()).to(sagaDlx).with(QUEUE_ORDER_STOCK_DECREASED_DLQ);
    }
    @Bean public Binding bindOrderStockDecreasedFailedDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueOrderStockDecreasedFailedDlq()).to(sagaDlx).with(QUEUE_ORDER_STOCK_DECREASED_FAILED_DLQ);
    }
    @Bean public Binding bindOrderStockRestoredDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueOrderStockRestoredDlq()).to(sagaDlx).with(QUEUE_ORDER_STOCK_RESTORED_DLQ);
    }
    @Bean public Binding bindOrderShipmentCreatedDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueOrderShipmentCreatedDlq()).to(sagaDlx).with(QUEUE_ORDER_SHIPMENT_CREATED_DLQ);
    }
    @Bean public Binding bindOrderShipmentCreationFailedDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueOrderShipmentCreationFailedDlq()).to(sagaDlx).with(QUEUE_ORDER_SHIPMENT_CREATION_FAILED_DLQ);
    }
    @Bean public Binding bindOrderShipmentCompletedDlq(DirectExchange sagaDlx) {
        return BindingBuilder.bind(queueOrderShipmentCompletedDlq()).to(sagaDlx).with(QUEUE_ORDER_SHIPMENT_COMPLETED_DLQ);
    }
}
