package com.shipflow.shipmentservice.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ShipmentCreatedEvent(
	UUID orderId,
	UUID shipmentId,
	UUID productId,
	int quantity,
	UUID departureHubId,
	UUID arrivalHubId,
	LocalDateTime requestDeadline,
	String requestNote,
	List<Route> routes
) {
	public record Route(int sequence, UUID departureHubId, UUID arrivalHubId) {
	}
}