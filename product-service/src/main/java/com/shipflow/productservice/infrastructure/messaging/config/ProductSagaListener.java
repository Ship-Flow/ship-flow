package com.shipflow.productservice.infrastructure.messaging.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shipflow.productservice.infrastructure.messaging.event.OrderCanceledEvent;
import com.shipflow.productservice.infrastructure.messaging.event.OrderCreationStartedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductSagaListener {

	private final OrderCreationStatedHandler orderCreationStatedHandler;
	private final StockRestoreHandler stockRestoreHandler;

	@RabbitListener(queues = ProductRabbitConfig.QUEUE_PRODUCT_ORDER_CREATION_STARTED)
	public void handleOrderCreationStartedEvent(OrderCreationStartedEvent event) {
		orderCreationStatedHandler.handle(event);
	}

	@RabbitListener(queues = ProductRabbitConfig.QUEUE_PRODUCT_STOCK_RESTORED)
	public void handleStockRestoredEvent(OrderCanceledEvent event) {
		stockRestoreHandler.handle(event);
	}

}
