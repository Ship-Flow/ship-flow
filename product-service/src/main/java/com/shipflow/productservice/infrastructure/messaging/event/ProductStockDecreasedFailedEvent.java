package com.shipflow.productservice.infrastructure.messaging.event;

import com.shipflow.common.messaging.event.SagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStockDecreasedFailedEvent extends SagaEvent {

	private static final String EVENT_TYPE = "product.stock.decreased.failed";

	private String orderId;
	private String productId;
	private String reason;

	public ProductStockDecreasedFailedEvent(String orderId, String productId, String reason) {
		super(EVENT_TYPE);
		this.orderId = orderId;
		this.productId = productId;
		this.reason = reason;
	}
}
