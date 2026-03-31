package com.shipflow.orderservice.application.event;

import java.util.UUID;

public class OrderCreationFailedEvent extends SagaEvent {

    private final UUID orderId;

    public OrderCreationFailedEvent(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() { return orderId; }
}
