package com.shipflow.orderservice.infrastructure.messaging.event.publish;

import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderCreatedEvent extends SagaEvent {

    private static final String EVENT_TYPE = "order.created";

    private UUID orderId;
    private UUID ordererId;
    private UUID supplierCompanyId;
    private UUID receiverCompanyId;
    private UUID productId;
    private int quantity;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private LocalDateTime requestDeadline;
    private String requestNote;

    public OrderCreatedEvent(UUID orderId, UUID ordererId, UUID supplierCompanyId, UUID receiverCompanyId,
                             UUID productId, int quantity,
                             UUID departureHubId, UUID arrivalHubId,
                             LocalDateTime requestDeadline, String requestNote) {
        super(EVENT_TYPE);
        this.orderId = orderId;
        this.ordererId = ordererId;
        this.supplierCompanyId = supplierCompanyId;
        this.receiverCompanyId = receiverCompanyId;
        this.productId = productId;
        this.quantity = quantity;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.requestDeadline = requestDeadline;
        this.requestNote = requestNote;
    }
}
