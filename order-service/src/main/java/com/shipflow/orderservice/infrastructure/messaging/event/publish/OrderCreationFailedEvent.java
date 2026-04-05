package com.shipflow.orderservice.infrastructure.messaging.event.publish;

import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderCreationFailedEvent extends SagaEvent {

    private static final String EVENT_TYPE = "order.creation.failed";

    private UUID orderId;
    private UUID productId;
    private int quantity;

    public OrderCreationFailedEvent(UUID orderId, UUID productId, int quantity) {
        super(EVENT_TYPE);
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
