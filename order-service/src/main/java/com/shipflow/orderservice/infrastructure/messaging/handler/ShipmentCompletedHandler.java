package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.config.message.RabbitMqConfig;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.event.inbound.ShipmentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentCompletedHandler extends AbstractSagaHandler<ShipmentCompletedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ORDER_SHIPMENT_COMPLETED)
    public void receive(ShipmentCompletedEvent event) {
        handle(event);
    }

    @Override
    protected void process(ShipmentCompletedEvent event) {
        orderCommandService.completeOrder(event.getOrderId());
    }
}
