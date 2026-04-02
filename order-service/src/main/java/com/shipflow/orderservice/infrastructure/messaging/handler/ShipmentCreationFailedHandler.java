package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.config.OrderRabbitConfig;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.ShipmentCreationFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentCreationFailedHandler extends AbstractSagaHandler<ShipmentCreationFailedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_SHIPMENT_CREATION_FAILED)
    public void receive(ShipmentCreationFailedEvent event) {
        handle(event);
    }

    @Override
    protected void process(ShipmentCreationFailedEvent event) {
        orderCommandService.failOrderByShipment(event.getOrderId());
    }
}
