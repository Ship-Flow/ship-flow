package com.shipflow.notificationservice.infrastructure.client.order;

import java.util.UUID;

public record OrderReadModelResponse(
	UUID orderId,
	String productId,
	String productName,
	String departureHubName,
	String arrivalHubName
) {
}