package com.shipflow.common.messaging.publisher;

import com.shipflow.common.messaging.event.SagaEvent;

public interface EventPublisher {
    void publish(SagaEvent event);
}
