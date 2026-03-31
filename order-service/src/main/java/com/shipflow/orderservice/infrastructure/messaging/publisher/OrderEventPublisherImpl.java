package com.shipflow.orderservice.infrastructure.messaging.publisher;

import com.shipflow.orderservice.application.event.OrderEventPublisher;
import com.shipflow.orderservice.application.event.SagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * global 모듈의 exchange 이름은 "saga.events"로 가정.
 * global 모듈 완성 후 RabbitMqConfig.EXCHANGE_SAGA 상수로 교체한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private static final String SAGA_EXCHANGE = "saga.events";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(SagaEvent event, String routingKey) {
        log.info("Saga 이벤트 발행: routingKey={}, event={}", routingKey, event.getClass().getSimpleName());
        rabbitTemplate.convertAndSend(SAGA_EXCHANGE, routingKey, event);
    }
}
