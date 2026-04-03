package com.shipflow.productservice.infrastructure.messaging;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCanceledEvent extends SagaEvent {
	private static final String EVENT_TYPE = "order.canceled";

	private String orderId;
	private String productId;
	private Integer quantity;

	public OrderCanceledEvent(String orderId, String productId, Integer quantity) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
	}
}
