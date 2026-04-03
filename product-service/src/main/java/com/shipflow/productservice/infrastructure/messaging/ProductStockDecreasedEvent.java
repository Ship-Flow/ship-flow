package com.shipflow.productservice.infrastructure.messaging;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStockDecreasedEvent extends SagaEvent {

	private static final String EVENT_TYPE = "product.stock.decreased";

	private String orderId;
	private String productId;
	private Integer quantity;

	public ProductStockDecreasedEvent(String orderId, String productId, Integer quantity) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;

	}
}
