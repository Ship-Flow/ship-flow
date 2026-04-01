package com.shipflow.common.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSagaHandler<T extends SagaEvent> implements SagaEventHandler<T> {

    @Override
    public final void handle(T event) {
        log.info("[SagaEvent] Received | type={} | eventId={} | occurredAt={}",
                event.getEventType(), event.getEventId(), event.getOccurredAt());
        try {
            process(event);
            log.info("[SagaEvent] Processed | type={} | eventId={}",
                    event.getEventType(), event.getEventId());
        } catch (Exception e) {
            log.error("[SagaEvent] Failed | type={} | eventId={} | error={}",
                    event.getEventType(), event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 구현할 비즈니스 로직.
     * 로깅은 AbstractSagaHandler가 자동 처리.
     */
    protected abstract void process(T event);
}
