package com.shipflow.productservice.infrastructure.messaging.config;

import org.springframework.stereotype.Component;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.common.messaging.publisher.EventPublisher;
import com.shipflow.productservice.application.service.ProductService;
import com.shipflow.productservice.infrastructure.messaging.OrderCreationStartedEvent;
import com.shipflow.productservice.infrastructure.messaging.ProductStockDecreasedEvent;
import com.shipflow.productservice.infrastructure.messaging.ProductStockDecreasedFailedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreationStatedHandler extends AbstractSagaHandler<OrderCreationStartedEvent> {
	private final ProductService productService;
	private final EventPublisher eventPublisher;

	@Override
	protected void process(OrderCreationStartedEvent event) {
		try {
			productService.decreaseStock(event.getProductId(), event.getQuantity());

			eventPublisher.publish(new ProductStockDecreasedEvent(
				event.getOrderId(),
				event.getProductId(),
				event.getQuantity()
			));
		} catch (Exception e) {
			eventPublisher.publish(new ProductStockDecreasedFailedEvent(
				event.getOrderId(),
				event.getProductId(),
				e.getMessage()
			));
		}
	}
}
