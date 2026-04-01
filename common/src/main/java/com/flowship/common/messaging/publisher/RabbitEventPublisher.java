package com.flowship.common.messaging.publisher;

import com.flowship.common.config.RabbitMqConfig;
import com.flowship.common.messaging.event.SagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(SagaEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,   // 1. Exchange Bean 지정 (saga.event)
                event.getEventType(),           // 2. Routing Key
                event                           // 3. 내용물
        );
    }
}
