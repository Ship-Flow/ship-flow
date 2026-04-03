package com.shipflow.productservice.infrastructure.messaging;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockRestoredFailedEvent extends SagaEvent {

	private static final String EVENT_TYPE = "stock.restored";

	private String orderId;
	private String productId;
	private String reason;

	public StockRestoredFailedEvent(String orderId, String productId, String reason) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.productId = productId;
		this.reason = reason;
	}
}
