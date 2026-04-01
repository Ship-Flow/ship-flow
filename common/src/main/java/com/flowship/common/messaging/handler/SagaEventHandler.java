package com.flowship.common.messaging.handler;

import com.flowship.common.messaging.event.SagaEvent;

public interface SagaEventHandler<T extends SagaEvent> {
    void handle(T event);
}
