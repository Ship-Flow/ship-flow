package com.shipflow.shipmentservice.infrastructure.messaging.handler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shipflow.shipmentservice.application.ShipmentService;
import com.shipflow.shipmentservice.application.dto.command.CreateShipmentCommand;
import com.shipflow.shipmentservice.infrastructure.messaging.config.ShipmentRabbitConfig;
import com.shipflow.shipmentservice.infrastructure.messaging.event.consume.OrderCreatedEvent;
import com.shipflow.shipmentservice.infrastructure.persistence.ProcessedSagaEventRepository;

@Component
public class OrderCreatedHandler extends IdempotentSagaHandler<OrderCreatedEvent> {

	private final ShipmentService shipmentService;

	public OrderCreatedHandler(
		ProcessedSagaEventRepository processedSagaEventRepository,
		IdempotentSagaExecutor sagaExecutor,
		ShipmentService shipmentService
	) {
		super(processedSagaEventRepository, sagaExecutor);
		this.shipmentService = shipmentService;
	}

	@RabbitListener(queues = ShipmentRabbitConfig.QUEUE_SHIPMENT_ORDER_CREATED)
	public void receive(OrderCreatedEvent event) {
		handle(event);
	}

	@Override
	protected void doProcess(OrderCreatedEvent event) {
		shipmentService.createShipment(new CreateShipmentCommand(
			event.getOrderId(),
			event.getOrdererId(),
			event.getProductId(),
			event.getQuantity(),
			event.getDepartureHubId(),
			event.getArrivalHubId(),
			event.getRequestDeadline(),
			event.getRequestNote(),
			event.getShipmentAddress()
		));
	}
}