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
public class ShipmentCreatedHandler {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = "#{T(com.shipflow.orderservice.infrastructure.messaging.handler.QueueConstants).ORDER_SHIPMENT_CREATED}")
    public void handle(ShipmentCreatedMessage message) {
        log.info("배송 생성 이벤트 수신: orderId={}, shipmentId={}", message.orderId(), message.shipmentId());
        orderCommandService.linkShipment(message.orderId(), message.shipmentId());
    }

    public record ShipmentCreatedMessage(UUID orderId, UUID shipmentId) {}
}
