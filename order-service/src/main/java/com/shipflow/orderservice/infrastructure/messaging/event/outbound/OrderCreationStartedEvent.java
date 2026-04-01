package com.shipflow.orderservice.infrastructure.messaging.event.outbound;

import com.shipflow.common.messaging.event.EventType;
import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderCreationStartedEvent extends SagaEvent {

    private UUID orderId;
    private UUID productId;
    private int quantity;

    public OrderCreationStartedEvent(UUID orderId, UUID productId, int quantity) {
        super(EventType.ORDER_CREATION_STARTED);
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
