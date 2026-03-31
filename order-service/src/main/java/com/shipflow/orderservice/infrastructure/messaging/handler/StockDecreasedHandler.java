package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.orderservice.application.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * product.stock.decreased 이벤트 수신.
 * 큐 이름은 global RabbitMqConfig.QUEUE_ORDER_STOCK_DECREASED 상수로 교체 예정.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDecreasedHandler {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = "#{T(com.shipflow.orderservice.infrastructure.messaging.handler.QueueConstants).ORDER_STOCK_DECREASED}")
    public void handle(StockDecreasedMessage message) {
        log.info("재고 감소 이벤트 수신: orderId={}", message.orderId());
        orderCommandService.confirmCreation(message.orderId());
    }

    public record StockDecreasedMessage(UUID orderId, UUID productId, int quantity) {}
}
