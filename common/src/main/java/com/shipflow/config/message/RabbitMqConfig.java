package com.shipflow.config.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shipflow.common.messaging.event.EventType;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // ── Exchange 이름 ──────────────────────────────
    public static final String SAGA_EXCHANGE = "saga.events";      // Topic Exchange 방식
    public static final String SAGA_DLX      = "saga.events.dlx";  // Direct Exchange 방식 -> 메시지 처리 실패 시 사용

    // ── Queue 이름 상수 ────────────────────────────
    public static final String QUEUE_PRODUCT_ORDER_CREATION_STARTED   = "product.order.creation.started";
    public static final String QUEUE_ORDER_STOCK_DECREASED            = "order.product.stock.decreased";
    public static final String QUEUE_ORDER_STOCK_DECREASED_FAILED     = "order.product.stock.decreased.failed";
    public static final String QUEUE_ORDER_STOCK_RESTORED             = "order.product.stock.restored";
    public static final String QUEUE_SHIPMENT_ORDER_CREATED           = "shipment.order.created";
    public static final String QUEUE_PRODUCT_ORDER_CREATED            = "product.order.created";
    public static final String QUEUE_SLACK_ORDER_CREATED              = "slack.order.created";
    public static final String QUEUE_PRODUCT_ORDER_CREATION_FAILED    = "product.order.creation.failed";
    public static final String QUEUE_PRODUCT_ORDER_CANCELED           = "product.order.canceled";
    public static final String QUEUE_SHIPMENT_ORDER_CANCELED          = "shipment.order.canceled";
    public static final String QUEUE_ORDER_SHIPMENT_CREATED           = "order.shipment.created";
    public static final String QUEUE_AI_SHIPMENT_CREATED              = "ai.shipment.created";
    public static final String QUEUE_ORDER_SHIPMENT_CREATION_FAILED   = "order.shipment.creation.failed";
    public static final String QUEUE_ORDER_SHIPMENT_COMPLETED         = "order.shipment.completed";

    // ── DLQ 이름 상수 ──────────────────────────────
    public static final String QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ  = QUEUE_PRODUCT_ORDER_CREATION_STARTED  + ".dlq";
    public static final String QUEUE_ORDER_STOCK_DECREASED_DLQ           = QUEUE_ORDER_STOCK_DECREASED           + ".dlq";
    public static final String QUEUE_ORDER_STOCK_DECREASED_FAILED_DLQ    = QUEUE_ORDER_STOCK_DECREASED_FAILED     + ".dlq";
    public static final String QUEUE_ORDER_STOCK_RESTORED_DLQ            = QUEUE_ORDER_STOCK_RESTORED             + ".dlq";
    public static final String QUEUE_SHIPMENT_ORDER_CREATED_DLQ          = QUEUE_SHIPMENT_ORDER_CREATED           + ".dlq";
    public static final String QUEUE_PRODUCT_ORDER_CREATED_DLQ           = QUEUE_PRODUCT_ORDER_CREATED            + ".dlq";
    public static final String QUEUE_SLACK_ORDER_CREATED_DLQ             = QUEUE_SLACK_ORDER_CREATED              + ".dlq";
    public static final String QUEUE_PRODUCT_ORDER_CREATION_FAILED_DLQ   = QUEUE_PRODUCT_ORDER_CREATION_FAILED    + ".dlq";
    public static final String QUEUE_PRODUCT_ORDER_CANCELED_DLQ          = QUEUE_PRODUCT_ORDER_CANCELED           + ".dlq";
    public static final String QUEUE_SHIPMENT_ORDER_CANCELED_DLQ         = QUEUE_SHIPMENT_ORDER_CANCELED          + ".dlq";
    public static final String QUEUE_ORDER_SHIPMENT_CREATED_DLQ          = QUEUE_ORDER_SHIPMENT_CREATED           + ".dlq";
    public static final String QUEUE_AI_SHIPMENT_CREATED_DLQ             = QUEUE_AI_SHIPMENT_CREATED              + ".dlq";
    public static final String QUEUE_ORDER_SHIPMENT_CREATION_FAILED_DLQ  = QUEUE_ORDER_SHIPMENT_CREATION_FAILED   + ".dlq";
    public static final String QUEUE_ORDER_SHIPMENT_COMPLETED_DLQ        = QUEUE_ORDER_SHIPMENT_COMPLETED         + ".dlq";

    // ── Exchange Bean ──────────────────────────────
    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange sagaDlx() {
        return new DirectExchange(SAGA_DLX, true, false);
    }

    // ── Queue 공통 생성 메서드 (DLQ 자동 연결) ───────
    private Queue durableQueue(String name) {  // 메세지가 유실되지 않는 유연한 Queue 구축
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", SAGA_DLX)
                .withArgument("x-dead-letter-routing-key", name + ".dlq")
                .build();
    }

    // ── DLQ 자체는 추가 DLX 불필요 ───────
    private Queue dlqQueue(String name) {
        return QueueBuilder.durable(name).build();
    }

    // ── Queue Bean ─────────────────────────────────
    @Bean public Queue queueProductOrderCreationStarted()  { return durableQueue(QUEUE_PRODUCT_ORDER_CREATION_STARTED); }
    @Bean public Queue queueOrderStockDecreased()          { return durableQueue(QUEUE_ORDER_STOCK_DECREASED); }
    @Bean public Queue queueOrderStockDecreasedFailed()    { return durableQueue(QUEUE_ORDER_STOCK_DECREASED_FAILED); }
    @Bean public Queue queueOrderStockRestored()           { return durableQueue(QUEUE_ORDER_STOCK_RESTORED); }
    @Bean public Queue queueShipmentOrderCreated()         { return durableQueue(QUEUE_SHIPMENT_ORDER_CREATED); }
    @Bean public Queue queueProductOrderCreated()          { return durableQueue(QUEUE_PRODUCT_ORDER_CREATED); }
    @Bean public Queue queueSlackOrderCreated()            { return durableQueue(QUEUE_SLACK_ORDER_CREATED); }
    @Bean public Queue queueProductOrderCreationFailed()   { return durableQueue(QUEUE_PRODUCT_ORDER_CREATION_FAILED); }
    @Bean public Queue queueProductOrderCanceled()         { return durableQueue(QUEUE_PRODUCT_ORDER_CANCELED); }
    @Bean public Queue queueShipmentOrderCanceled()        { return durableQueue(QUEUE_SHIPMENT_ORDER_CANCELED); }
    @Bean public Queue queueOrderShipmentCreated()         { return durableQueue(QUEUE_ORDER_SHIPMENT_CREATED); }
    @Bean public Queue queueAiShipmentCreated()            { return durableQueue(QUEUE_AI_SHIPMENT_CREATED); }
    @Bean public Queue queueOrderShipmentCreationFailed()  { return durableQueue(QUEUE_ORDER_SHIPMENT_CREATION_FAILED); }
    @Bean public Queue queueOrderShipmentCompleted()       { return durableQueue(QUEUE_ORDER_SHIPMENT_COMPLETED); }

    // ── DLQ Queue Bean ─────────────────────────────
    @Bean public Queue queueProductOrderCreationStartedDlq()  { return dlqQueue(QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ); }
    @Bean public Queue queueOrderStockDecreasedDlq()          { return dlqQueue(QUEUE_ORDER_STOCK_DECREASED_DLQ); }
    @Bean public Queue queueOrderStockDecreasedFailedDlq()    { return dlqQueue(QUEUE_ORDER_STOCK_DECREASED_FAILED_DLQ); }
    @Bean public Queue queueOrderStockRestoredDlq()           { return dlqQueue(QUEUE_ORDER_STOCK_RESTORED_DLQ); }
    @Bean public Queue queueShipmentOrderCreatedDlq()         { return dlqQueue(QUEUE_SHIPMENT_ORDER_CREATED_DLQ); }
    @Bean public Queue queueProductOrderCreatedDlq()          { return dlqQueue(QUEUE_PRODUCT_ORDER_CREATED_DLQ); }
    @Bean public Queue queueSlackOrderCreatedDlq()            { return dlqQueue(QUEUE_SLACK_ORDER_CREATED_DLQ); }
    @Bean public Queue queueProductOrderCreationFailedDlq()   { return dlqQueue(QUEUE_PRODUCT_ORDER_CREATION_FAILED_DLQ); }
    @Bean public Queue queueProductOrderCanceledDlq()         { return dlqQueue(QUEUE_PRODUCT_ORDER_CANCELED_DLQ); }
    @Bean public Queue queueShipmentOrderCanceledDlq()        { return dlqQueue(QUEUE_SHIPMENT_ORDER_CANCELED_DLQ); }
    @Bean public Queue queueOrderShipmentCreatedDlq()         { return dlqQueue(QUEUE_ORDER_SHIPMENT_CREATED_DLQ); }
    @Bean public Queue queueAiShipmentCreatedDlq()            { return dlqQueue(QUEUE_AI_SHIPMENT_CREATED_DLQ); }
    @Bean public Queue queueOrderShipmentCreationFailedDlq()  { return dlqQueue(QUEUE_ORDER_SHIPMENT_CREATION_FAILED_DLQ); }
    @Bean public Queue queueOrderShipmentCompletedDlq()       { return dlqQueue(QUEUE_ORDER_SHIPMENT_COMPLETED_DLQ); }

    // ── Binding Bean ───────────────────────────────
    @Bean public Binding bindProductOrderCreationStarted() {
        return BindingBuilder.bind(queueProductOrderCreationStarted())
                .to(sagaExchange())
                .with(EventType.ORDER_CREATION_STARTED);
    }
    @Bean public Binding bindOrderStockDecreased() {
        return BindingBuilder.bind(queueOrderStockDecreased())
                .to(sagaExchange())
                .with(EventType.PRODUCT_STOCK_DECREASED);
    }
    @Bean public Binding bindOrderStockDecreasedFailed() {
        return BindingBuilder.bind(queueOrderStockDecreasedFailed())
                .to(sagaExchange())
                .with(EventType.PRODUCT_STOCK_DECREASED_FAILED);
    }
    @Bean public Binding bindOrderStockRestored() {
        return BindingBuilder.bind(queueOrderStockRestored())
                .to(sagaExchange())
                .with(EventType.PRODUCT_STOCK_RESTORED);
    }
    @Bean public Binding bindShipmentOrderCreated() {
        return BindingBuilder.bind(queueShipmentOrderCreated())
                .to(sagaExchange())
                .with(EventType.ORDER_CREATED);
    }
    @Bean public Binding bindProductOrderCreated() {
        return BindingBuilder.bind(queueProductOrderCreated())
                .to(sagaExchange())
                .with(EventType.ORDER_CREATED);
    }
    @Bean public Binding bindSlackOrderCreated() {
        return BindingBuilder.bind(queueSlackOrderCreated())
                .to(sagaExchange())
                .with(EventType.ORDER_CREATED);
    }
    @Bean public Binding bindProductOrderCreationFailed() {
        return BindingBuilder.bind(queueProductOrderCreationFailed())
                .to(sagaExchange())
                .with(EventType.ORDER_CREATION_FAILED);
    }
    @Bean public Binding bindProductOrderCanceled() {
        return BindingBuilder.bind(queueProductOrderCanceled())
                .to(sagaExchange())
                .with(EventType.ORDER_CANCELED);
    }
    @Bean public Binding bindShipmentOrderCanceled() {
        return BindingBuilder.bind(queueShipmentOrderCanceled())
                .to(sagaExchange())
                .with(EventType.ORDER_CANCELED);
    }
    @Bean public Binding bindOrderShipmentCreated() {
        return BindingBuilder.bind(queueOrderShipmentCreated())
                .to(sagaExchange())
                .with(EventType.SHIPMENT_CREATED);
    }
    @Bean public Binding bindAiShipmentCreated() {
        return BindingBuilder.bind(queueAiShipmentCreated())
                .to(sagaExchange())
                .with(EventType.SHIPMENT_CREATED);
    }
    @Bean public Binding bindOrderShipmentCreationFailed() {
        return BindingBuilder.bind(queueOrderShipmentCreationFailed())
                .to(sagaExchange())
                .with(EventType.SHIPMENT_CREATION_FAILED);
    }
    @Bean public Binding bindOrderShipmentCompleted() {
        return BindingBuilder.bind(queueOrderShipmentCompleted())
                .to(sagaExchange())
                .with(EventType.SHIPMENT_COMPLETED);
    }

    // ── DLQ Binding Bean (sagaDlx → DLQ) ──────────
    @Bean public Binding bindProductOrderCreationStartedDlq() {
        return BindingBuilder.bind(queueProductOrderCreationStartedDlq()).to(sagaDlx()).with(QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ);
    }
    @Bean public Binding bindOrderStockDecreasedDlq() {
        return BindingBuilder.bind(queueOrderStockDecreasedDlq()).to(sagaDlx()).with(QUEUE_ORDER_STOCK_DECREASED_DLQ);
    }
    @Bean public Binding bindOrderStockDecreasedFailedDlq() {
        return BindingBuilder.bind(queueOrderStockDecreasedFailedDlq()).to(sagaDlx()).with(QUEUE_ORDER_STOCK_DECREASED_FAILED_DLQ);
    }
    @Bean public Binding bindOrderStockRestoredDlq() {
        return BindingBuilder.bind(queueOrderStockRestoredDlq()).to(sagaDlx()).with(QUEUE_ORDER_STOCK_RESTORED_DLQ);
    }
    @Bean public Binding bindShipmentOrderCreatedDlq() {
        return BindingBuilder.bind(queueShipmentOrderCreatedDlq()).to(sagaDlx()).with(QUEUE_SHIPMENT_ORDER_CREATED_DLQ);
    }
    @Bean public Binding bindProductOrderCreatedDlq() {
        return BindingBuilder.bind(queueProductOrderCreatedDlq()).to(sagaDlx()).with(QUEUE_PRODUCT_ORDER_CREATED_DLQ);
    }
    @Bean public Binding bindSlackOrderCreatedDlq() {
        return BindingBuilder.bind(queueSlackOrderCreatedDlq()).to(sagaDlx()).with(QUEUE_SLACK_ORDER_CREATED_DLQ);
    }
    @Bean public Binding bindProductOrderCreationFailedDlq() {
        return BindingBuilder.bind(queueProductOrderCreationFailedDlq()).to(sagaDlx()).with(QUEUE_PRODUCT_ORDER_CREATION_FAILED_DLQ);
    }
    @Bean public Binding bindProductOrderCanceledDlq() {
        return BindingBuilder.bind(queueProductOrderCanceledDlq()).to(sagaDlx()).with(QUEUE_PRODUCT_ORDER_CANCELED_DLQ);
    }
    @Bean public Binding bindShipmentOrderCanceledDlq() {
        return BindingBuilder.bind(queueShipmentOrderCanceledDlq()).to(sagaDlx()).with(QUEUE_SHIPMENT_ORDER_CANCELED_DLQ);
    }
    @Bean public Binding bindOrderShipmentCreatedDlq() {
        return BindingBuilder.bind(queueOrderShipmentCreatedDlq()).to(sagaDlx()).with(QUEUE_ORDER_SHIPMENT_CREATED_DLQ);
    }
    @Bean public Binding bindAiShipmentCreatedDlq() {
        return BindingBuilder.bind(queueAiShipmentCreatedDlq()).to(sagaDlx()).with(QUEUE_AI_SHIPMENT_CREATED_DLQ);
    }
    @Bean public Binding bindOrderShipmentCreationFailedDlq() {
        return BindingBuilder.bind(queueOrderShipmentCreationFailedDlq()).to(sagaDlx()).with(QUEUE_ORDER_SHIPMENT_CREATION_FAILED_DLQ);
    }
    @Bean public Binding bindOrderShipmentCompletedDlq() {
        return BindingBuilder.bind(queueOrderShipmentCompletedDlq()).to(sagaDlx()).with(QUEUE_ORDER_SHIPMENT_COMPLETED_DLQ);
    }

    // ── RabbitTemplate (Jackson 직렬화) ───────────
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, ObjectMapper objectMapper) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return template;
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
