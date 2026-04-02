package com.shipflow.common.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;

public interface SagaEventHandler<T extends SagaEvent> {
    void handle(T event);
}
