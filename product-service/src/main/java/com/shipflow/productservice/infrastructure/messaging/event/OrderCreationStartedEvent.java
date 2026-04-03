package com.shipflow.productservice.infrastructure.messaging.event;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreationStartedEvent extends SagaEvent {

	private static final String EVENT_TYPE = "order.creation.started";

	private String orderId;
	private String productId;
	private Integer quantity;

	public OrderCreationStartedEvent(String orderId, String productId, Integer quantity) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
	}
}
