package com.flowship.common.messaging.publisher;

import com.flowship.common.messaging.event.SagaEvent;

public interface EventPublisher {
    void publish(SagaEvent event);
}
