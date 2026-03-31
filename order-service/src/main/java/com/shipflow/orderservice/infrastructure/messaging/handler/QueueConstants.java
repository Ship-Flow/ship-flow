package com.shipflow.orderservice.infrastructure.messaging.handler;

/**
 * 임시 큐 이름 상수.
 * global 모듈 완성 후 global RabbitMqConfig의 상수로 전부 교체한다.
 */
public final class QueueConstants {

    private QueueConstants() {}

    public static final String ORDER_STOCK_DECREASED = "order.stock.decreased";
    public static final String ORDER_STOCK_DECREASED_FAILED = "order.stock.decreased.failed";
    public static final String ORDER_STOCK_RESTORED = "order.stock.restored";
    public static final String ORDER_SHIPMENT_CREATED = "order.shipment.created";
    public static final String ORDER_SHIPMENT_CREATION_FAILED = "order.shipment.creation.failed";
    public static final String ORDER_SHIPMENT_COMPLETED = "order.shipment.completed";
}
