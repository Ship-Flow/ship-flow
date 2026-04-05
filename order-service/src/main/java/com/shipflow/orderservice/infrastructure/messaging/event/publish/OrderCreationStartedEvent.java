package com.shipflow.orderservice.infrastructure.messaging.event.publish;

import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderCreationStartedEvent extends SagaEvent {

    private static final String EVENT_TYPE = "order.creation.started";

    private UUID orderId;
    private UUID productId;
    private Integer quantity;

    public OrderCreationStartedEvent(UUID orderId, UUID productId, Integer quantity) {
        super(EVENT_TYPE);
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
