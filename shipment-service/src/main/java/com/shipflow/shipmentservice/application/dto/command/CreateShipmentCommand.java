package com.shipflow.shipmentservice.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateShipmentCommand(
	UUID orderId,
	UUID ordererId,
	UUID productId,
	int quantity,
	UUID departureHubId,
	UUID arrivalHubId,
	LocalDateTime requestDeadline,
	String requestNote,
	String shipmentAddress
) {
}
