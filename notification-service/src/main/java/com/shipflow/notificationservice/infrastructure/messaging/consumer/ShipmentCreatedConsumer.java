package com.shipflow.notificationservice.infrastructure.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shipflow.notificationservice.application.NotificationOrchestratorService;
import com.shipflow.notificationservice.infrastructure.messaging.config.NotificationRabbitConfig;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentCreatedConsumer {

	private final NotificationOrchestratorService notificationOrchestratorService;

	@RabbitListener(queues = NotificationRabbitConfig.QUEUE_NOTIFICATION_SHIPMENT_CREATED)
	public void handleShipmentCreated(ShipmentCreatedEvent event) {
		log.info(
			"[ShipmentCreatedConsumer] shipment.created 수신 - shipmentId={}, orderId={}, receiverSlackId={}",
			event.getShipmentId(),
			event.getOrderId(),
			event.getReceiverSlackId()
		);

		notificationOrchestratorService.handleShipmentCreated(event);
	}
}