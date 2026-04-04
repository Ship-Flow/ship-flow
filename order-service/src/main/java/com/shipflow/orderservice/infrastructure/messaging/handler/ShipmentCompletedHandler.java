package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.config.OrderRabbitConfig;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.ShipmentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentCompletedHandler extends IdempotentSagaHandler<ShipmentCompletedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_SHIPMENT_COMPLETED)
    public void receive(ShipmentCompletedEvent event) {
        handle(event);
    }

    @Override
    protected void doProcess(ShipmentCompletedEvent event) {
        orderCommandService.completeOrder(event.getOrderId());
    }
}
