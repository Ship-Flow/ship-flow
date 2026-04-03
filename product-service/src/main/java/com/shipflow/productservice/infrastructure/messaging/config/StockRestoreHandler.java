package com.shipflow.productservice.infrastructure.messaging.config;

import org.springframework.stereotype.Component;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.common.messaging.publisher.EventPublisher;
import com.shipflow.productservice.application.service.ProductService;
import com.shipflow.productservice.infrastructure.messaging.event.OrderCanceledEvent;
import com.shipflow.productservice.infrastructure.messaging.event.StockRestoredEvent;
import com.shipflow.productservice.infrastructure.messaging.event.StockRestoredFailedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockRestoreHandler extends AbstractSagaHandler<OrderCanceledEvent> {
	private final ProductService productService;
	private final EventPublisher eventPublisher;

	@Override
	public void process(OrderCanceledEvent event) {
		try {
			productService.restoreStock(event.getProductId(), event.getQuantity());

		} catch (Exception e) {
			eventPublisher.publish(new StockRestoredFailedEvent(
				event.getOrderId(), event.getProductId(), e.getMessage()));
		}

		eventPublisher.publish(new StockRestoredEvent(
			event.getOrderId(), event.getProductId(), event.getQuantity()));
	}
}
