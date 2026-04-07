package com.shipflow.shipmentservice.domain.event;

import java.util.UUID;

public record ShipmentCreationFailedEvent(UUID orderId) {
}