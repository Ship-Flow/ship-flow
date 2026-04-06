package com.shipflow.productservice.infrastructure.messaging.event;

import java.util.UUID;

public record UpdateStockEvent(
	UUID productId,
	int stock
) {
}
