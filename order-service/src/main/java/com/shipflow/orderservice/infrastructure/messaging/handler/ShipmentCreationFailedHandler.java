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
public class ShipmentCreationFailedHandler {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = "#{T(com.shipflow.orderservice.infrastructure.messaging.handler.QueueConstants).ORDER_SHIPMENT_CREATION_FAILED}")
    public void handle(ShipmentCreationFailedMessage message) {
        log.info("배송 생성 실패 이벤트 수신: orderId={}", message.orderId());
        orderCommandService.failOrder(message.orderId());
    }

    public record ShipmentCreationFailedMessage(UUID orderId) {}
}
