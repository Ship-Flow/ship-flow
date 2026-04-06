package com.shipflow.notificationservice.infrastructure.client.order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderReadModelResponse(
	UUID orderId,
	UUID productId,
	String productName,
	int quantity,
	String ordererName,
	LocalDateTime createdAt,
	String departureHubName,
	String arrivalHubName,
	LocalDateTime requestDeadline,
	String requestNote
) {
}