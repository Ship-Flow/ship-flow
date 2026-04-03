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
}
