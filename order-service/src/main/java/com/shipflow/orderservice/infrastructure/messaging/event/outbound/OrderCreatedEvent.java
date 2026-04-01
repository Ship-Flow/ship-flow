package com.shipflow.orderservice.infrastructure.messaging.event.outbound;

import com.shipflow.common.messaging.event.EventType;
import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderCreatedEvent extends SagaEvent {

    private UUID orderId;
    private UUID supplierCompanyId;
    private UUID receiverCompanyId;
    private UUID productId;
    private int quantity;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private LocalDateTime requestDeadline;

    public OrderCreatedEvent(UUID orderId, UUID supplierCompanyId, UUID receiverCompanyId,
                             UUID productId, int quantity,
                             UUID departureHubId, UUID arrivalHubId,
                             LocalDateTime requestDeadline) {
        super(EventType.ORDER_CREATED);
        this.orderId = orderId;
        this.supplierCompanyId = supplierCompanyId;
        this.receiverCompanyId = receiverCompanyId;
        this.productId = productId;
        this.quantity = quantity;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.requestDeadline = requestDeadline;
    }
}
