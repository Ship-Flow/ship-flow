package com.shipflow.orderservice.domain.event;

import java.util.UUID;

public record OrderShipmentLinkedEvent(
        UUID orderId,
        UUID shipmentId,
        String shipmentStatus,
        UUID departureHubId,
        String departureHubName,
        UUID arrivalHubId,
        String arrivalHubName
) {}
