package com.flowship.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowship.common.messaging.event.EventType;
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
    public static final String SAGA_DLX      = "saga.events.dlx";  // Direct Exchange 방식 -> 메세지 처리 실패 시 사용

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
