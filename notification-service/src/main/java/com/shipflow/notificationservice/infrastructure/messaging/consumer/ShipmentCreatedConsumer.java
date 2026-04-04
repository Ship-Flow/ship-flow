package com.shipflow.notificationservice.infrastructure.messaging.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.shipflow.notificationservice.infrastructure.messaging.config.NotificationRabbitConfig;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShipmentCreatedConsumer {

	@RabbitListener(queues = NotificationRabbitConfig.QUEUE_NOTIFICATION_SHIPMENT_CREATED)
	public void handleShipmentCreated(ShipmentCreatedEvent event) {
		log.info(
			"[ShipmentCreatedConsumer] shipment.created 수신 - orderId={}, ordererId={}, supplierCompanyId={}, receiverCompanyId={}, productId={}, quantity={}, departureHubId={}, arrivalHubId={}, requestDeadline={}, requestNote={}, occurredAt={}",
			event.getOrderId(),
			event.getOrdererId(),
			event.getSupplierCompanyId(),
			event.getReceiverCompanyId(),
			event.getProductId(),
			event.getQuantity(),
			event.getDepartureHubId(),
			event.getArrivalHubId(),
			event.getRequestDeadline(),
			event.getRequestNote(),
			event.getOccurredAt()
		);
	}
}