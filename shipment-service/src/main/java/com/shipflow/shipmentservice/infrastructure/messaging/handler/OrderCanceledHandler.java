package com.shipflow.shipmentservice.infrastructure.messaging.handler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shipflow.shipmentservice.application.ShipmentService;
import com.shipflow.shipmentservice.infrastructure.messaging.config.ShipmentRabbitConfig;
import com.shipflow.shipmentservice.infrastructure.messaging.event.consume.OrderCanceledEvent;
import com.shipflow.shipmentservice.infrastructure.persistence.ProcessedSagaEventRepository;

@Component
public class OrderCanceledHandler extends IdempotentSagaHandler<OrderCanceledEvent> {

	private final ShipmentService shipmentService;

	public OrderCanceledHandler(
		ProcessedSagaEventRepository processedSagaEventRepository,
		IdempotentSagaExecutor sagaExecutor,
		ShipmentService shipmentService
	) {
		super(processedSagaEventRepository, sagaExecutor);
		this.shipmentService = shipmentService;
	}

	@RabbitListener(queues = ShipmentRabbitConfig.QUEUE_SHIPMENT_ORDER_CANCELED)
	public void receive(OrderCanceledEvent event) {
		handle(event);
	}

	@Override
	protected void doProcess(OrderCanceledEvent event) {
		shipmentService.cancelShipment(event.getOrderId());
	}
}
