package com.shipflow.orderservice.application.event;

import java.util.UUID;

public class OrderCanceledEvent extends SagaEvent {

    private final UUID orderId;
    private final UUID productId;
    private final int quantity;

    public OrderCanceledEvent(UUID orderId, UUID productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
