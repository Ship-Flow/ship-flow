package com.flowship.common.messaging.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class SagaEvent {

    private final String eventId;        // 멱등성 처리용
    private final String eventType;      // EventType 상수값
    private final LocalDateTime occurredAt;

    protected SagaEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredAt = LocalDateTime.now();
    }
}
