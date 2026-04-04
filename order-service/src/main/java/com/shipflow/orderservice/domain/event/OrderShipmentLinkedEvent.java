package com.shipflow.orderservice.domain.event;

import com.shipflow.orderservice.domain.model.ShipmentStatus;

import java.util.UUID;

public record OrderShipmentLinkedEvent(
        UUID orderId,
        UUID shipmentId,
        ShipmentStatus shipmentStatus,
        UUID departureHubId,
        String departureHubName,
        UUID arrivalHubId,
        String arrivalHubName
) {}
