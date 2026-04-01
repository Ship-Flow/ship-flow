package com.shipflow.global.messaging.handler;

import com.shipflow.global.messaging.event.SagaEvent;

public interface SagaEventHandler<T extends SagaEvent> {
    void handle(T event);
}
