package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.orderservice.application.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockDecreasedFailedHandler {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = "#{T(com.shipflow.orderservice.infrastructure.messaging.handler.QueueConstants).ORDER_STOCK_DECREASED_FAILED}")
    public void handle(StockDecreasedFailedMessage message) {
        log.info("재고 감소 실패 이벤트 수신: orderId={}", message.orderId());
        orderCommandService.failOrder(message.orderId());
    }

    public record StockDecreasedFailedMessage(UUID orderId) {}
}
