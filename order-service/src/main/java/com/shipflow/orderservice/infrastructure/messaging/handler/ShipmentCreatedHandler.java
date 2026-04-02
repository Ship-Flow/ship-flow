package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.config.OrderRabbitConfig;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.ShipmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentCreatedHandler extends AbstractSagaHandler<ShipmentCreatedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_SHIPMENT_CREATED)
    public void receive(ShipmentCreatedEvent event) {
        handle(event);
    }

    @Override
    protected void process(ShipmentCreatedEvent event) {
        orderCommandService.linkShipment(event.getOrderId(), event.getShipmentId());
    }
}
