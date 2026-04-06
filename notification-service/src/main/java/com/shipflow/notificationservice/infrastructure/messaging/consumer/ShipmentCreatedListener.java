package com.shipflow.notificationservice.infrastructure.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shipflow.notificationservice.infrastructure.messaging.config.NotificationRabbitConfig;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShipmentCreatedListener {

	private final ShipmentCreatedHandler shipmentCreatedHandler;

	@RabbitListener(queues = NotificationRabbitConfig.QUEUE_NOTIFICATION_SHIPMENT_CREATED)
	public void onShipmentCreated(ShipmentCreatedEvent event) {
		shipmentCreatedHandler.handle(event);
	}
}