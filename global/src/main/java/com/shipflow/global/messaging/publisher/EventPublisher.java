package com.shipflow.global.messaging.publisher;

import com.shipflow.global.messaging.event.SagaEvent;

public interface EventPublisher {
    void publish(SagaEvent event);
}
