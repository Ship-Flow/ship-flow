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
public class ShipmentCompletedHandler {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = "#{T(com.shipflow.orderservice.infrastructure.messaging.handler.QueueConstants).ORDER_SHIPMENT_COMPLETED}")
    public void handle(ShipmentCompletedMessage message) {
        log.info("배송 완료 이벤트 수신: orderId={}", message.orderId());
        orderCommandService.completeOrder(message.orderId());
    }

    public record ShipmentCompletedMessage(UUID orderId) {}
}
