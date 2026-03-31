package com.shipflow.orderservice.infrastructure.messaging.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class StockRestoredHandler {

    @RabbitListener(queues = "#{T(com.shipflow.orderservice.infrastructure.messaging.handler.QueueConstants).ORDER_STOCK_RESTORED}")
    public void handle(StockRestoredMessage message) {
        log.info("재고 복원 완료 이벤트 수신 (보상 트랜잭션 확인): orderId={}", message.orderId());
    }

    public record StockRestoredMessage(UUID orderId, UUID productId, int quantity) {}
}
