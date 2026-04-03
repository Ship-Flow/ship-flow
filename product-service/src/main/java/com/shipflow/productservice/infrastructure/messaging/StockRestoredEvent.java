package com.shipflow.productservice.infrastructure.messaging;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockRestoredEvent extends SagaEvent {

	private static final String EVENT_TYPE = "stock.restored";

	private Object orderId;
	private Object productId;
	private Object quantity;

	public StockRestoredEvent(Object orderId, Object productId, Object quantity) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
	}
}
