package com.flowship.common.messaging.event;

/**
 * Saga 이벤트 타입 상수.
 * enum 대신 String 상수로 정의 — Jackson 역직렬화 시 enum 불일치 예외 방지
 */
public final class EventType {

    // Order
    public static final String ORDER_CREATION_STARTED         = "order.creation.started";
    public static final String ORDER_CREATED                  = "order.created";
    public static final String ORDER_CREATION_FAILED          = "order.creation.failed";
    public static final String ORDER_CANCELED                 = "order.canceled";

    // Product
    public static final String PRODUCT_STOCK_DECREASED        = "product.stock.decreased";
    public static final String PRODUCT_STOCK_DECREASED_FAILED = "product.stock.decreased.failed";
    public static final String PRODUCT_STOCK_RESTORED         = "product.stock.restored";

    // Shipment
    public static final String SHIPMENT_CREATED               = "shipment.created";
    public static final String SHIPMENT_CREATION_FAILED       = "shipment.creation.failed";
    public static final String SHIPMENT_COMPLETED             = "shipment.completed";

    private EventType() {}
}
