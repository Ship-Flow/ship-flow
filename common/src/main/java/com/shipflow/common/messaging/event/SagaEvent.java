package com.shipflow.common.messaging.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class SagaEvent {

    private String eventId;        // 멱등성 처리용
    private String eventType;      // EventType 상수값
    private LocalDateTime occurredAt;

    protected SagaEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredAt = LocalDateTime.now();
    }

    protected SagaEvent() {
        // Jackson 역직렬화시 빈생성자가 필요
    }
}
