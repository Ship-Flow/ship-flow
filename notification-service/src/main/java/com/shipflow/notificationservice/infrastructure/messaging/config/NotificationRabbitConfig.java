package com.shipflow.notificationservice.infrastructure.messaging.config;

import static org.springframework.amqp.core.BindingBuilder.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shipflow.config.message.RabbitMqConfig;

@Configuration
public class NotificationRabbitConfig {

	private static final String ROUTING_SHIPMENT_CREATED = "shipment.created";

	public static final String QUEUE_NOTIFICATION_SHIPMENT_CREATED = "notification.shipment.created";
	public static final String QUEUE_NOTIFICATION_SHIPMENT_CREATED_DLQ =
		QUEUE_NOTIFICATION_SHIPMENT_CREATED + ".dlq";

	@Bean
	public Queue queueNotificationShipmentCreated() {
		return RabbitMqConfig.durableQueue(QUEUE_NOTIFICATION_SHIPMENT_CREATED);
	}

	@Bean
	public Queue queueNotificationShipmentCreatedDlq() {
		return RabbitMqConfig.dlqQueue(QUEUE_NOTIFICATION_SHIPMENT_CREATED_DLQ);
	}

	@Bean
	public Binding bindNotificationShipmentCreated(TopicExchange sagaExchange) {
		return bind(queueNotificationShipmentCreated())
			.to(sagaExchange)
			.with(ROUTING_SHIPMENT_CREATED);
	}

	@Bean
	public Binding bindNotificationShipmentCreatedDlq(DirectExchange sagaDlx) {
		return bind(queueNotificationShipmentCreatedDlq())
			.to(sagaDlx)
			.with(QUEUE_NOTIFICATION_SHIPMENT_CREATED_DLQ);
	}
}