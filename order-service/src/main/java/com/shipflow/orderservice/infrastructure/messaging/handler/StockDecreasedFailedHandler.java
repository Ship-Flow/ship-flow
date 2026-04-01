package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.config.message.RabbitMqConfig;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.event.inbound.ProductStockDecreasedFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreasedFailedHandler extends AbstractSagaHandler<ProductStockDecreasedFailedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ORDER_STOCK_DECREASED_FAILED)
    public void receive(ProductStockDecreasedFailedEvent event) {
        handle(event);
    }

    @Override
    protected void process(ProductStockDecreasedFailedEvent event) {
        orderCommandService.failOrder(event.getOrderId());
    }
}
