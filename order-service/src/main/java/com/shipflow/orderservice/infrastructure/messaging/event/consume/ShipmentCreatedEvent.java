package com.shipflow.orderservice.infrastructure.messaging.event.consume;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentCreatedEvent extends SagaEvent {

    private UUID orderId;
    private UUID shipmentId;
    private String shipmentStatus;
    private UUID departureHubId;
    private String departureHubName;
    private UUID arrivalHubId;
    private String arrivalHubName;

    public ShipmentCreatedEvent(UUID orderId, UUID shipmentId, String shipmentStatus,
                                UUID departureHubId, String departureHubName,
                                UUID arrivalHubId, String arrivalHubName) {
        super("shipment.created");
        this.orderId = orderId;
        this.shipmentId = shipmentId;
        this.shipmentStatus = shipmentStatus;
        this.departureHubId = departureHubId;
        this.departureHubName = departureHubName;
        this.arrivalHubId = arrivalHubId;
        this.arrivalHubName = arrivalHubName;
    }
}
