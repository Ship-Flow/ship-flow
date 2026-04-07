package com.shipflow.shipmentservice.domain.event;

import java.util.UUID;

public record ShipmentCompletedEvent(UUID orderId, UUID shipmentId) {
}
