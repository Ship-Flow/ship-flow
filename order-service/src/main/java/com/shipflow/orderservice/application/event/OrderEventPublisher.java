package com.shipflow.orderservice.application.event;

/**
 * Application 레이어에서 의존하는 이벤트 발행 포트.
 * 실제 구현은 infrastructure/messaging/publisher에 위치한다.
 */
public interface OrderEventPublisher {
    void publish(SagaEvent event, String routingKey);
}
