package com.shipflow.notificationservice.infrastructure.messaging.consumer;

import java.time.Duration;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.notificationservice.application.NotificationOrchestratorService;
import com.shipflow.notificationservice.infrastructure.messaging.config.NotificationRabbitConfig;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShipmentCreatedHandler extends AbstractSagaHandler<ShipmentCreatedEvent> {

	private final NotificationOrchestratorService notificationOrchestratorService;
	private final RedisTemplate<String, String> redisTemplate;
	private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

	public ShipmentCreatedHandler(
		NotificationOrchestratorService notificationOrchestratorService,
		RedisTemplate<String, String> redisTemplate
	) {
		this.notificationOrchestratorService = notificationOrchestratorService;
		this.redisTemplate = redisTemplate;
	}

	@RabbitListener(queues = NotificationRabbitConfig.QUEUE_NOTIFICATION_SHIPMENT_CREATED)
	public void receive(ShipmentCreatedEvent event) {
		handle(event);
	}

	@Override
	protected void process(ShipmentCreatedEvent event) {
		String key = "saga:processed:" + event.getEventId();

		Boolean isNew = redisTemplate.opsForValue()
			.setIfAbsent(key, "1", IDEMPOTENCY_TTL);

		if (Boolean.FALSE.equals(isNew)) {
			log.warn("중복 이벤트 무시 eventId={}", event.getEventId());
			return;
		}

		try {
			notificationOrchestratorService.handleShipmentCreated(event);
		} catch (Exception e) {
			redisTemplate.delete(key);
			throw e;
		}
	}
}