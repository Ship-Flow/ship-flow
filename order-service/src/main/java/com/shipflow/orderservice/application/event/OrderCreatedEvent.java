package com.shipflow.orderservice.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCreatedEvent extends SagaEvent {

    private final UUID orderId;
    private final UUID supplierCompanyId;
    private final UUID receiverCompanyId;
    private final UUID productId;
    private final int quantity;
    private final UUID departureHubId;
    private final UUID arrivalHubId;
    private final LocalDateTime requestDeadline;

    public OrderCreatedEvent(
            UUID orderId,
            UUID supplierCompanyId,
            UUID receiverCompanyId,
            UUID productId,
            int quantity,
            UUID departureHubId,
            UUID arrivalHubId,
            LocalDateTime requestDeadline
    ) {
        this.orderId = orderId;
        this.supplierCompanyId = supplierCompanyId;
        this.receiverCompanyId = receiverCompanyId;
        this.productId = productId;
        this.quantity = quantity;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.requestDeadline = requestDeadline;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getSupplierCompanyId() { return supplierCompanyId; }
    public UUID getReceiverCompanyId() { return receiverCompanyId; }
    public UUID getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public UUID getDepartureHubId() { return departureHubId; }
    public UUID getArrivalHubId() { return arrivalHubId; }
    public LocalDateTime getRequestDeadline() { return requestDeadline; }
}
