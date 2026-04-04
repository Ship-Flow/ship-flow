package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.config.OrderRabbitConfig;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.ProductStockDecreasedFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreasedFailedHandler extends IdempotentSagaHandler<ProductStockDecreasedFailedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_STOCK_DECREASED_FAILED)
    public void receive(ProductStockDecreasedFailedEvent event) {
        handle(event);
    }

    @Override
    protected void doProcess(ProductStockDecreasedFailedEvent event) {
        orderCommandService.failOrder(event.getOrderId());
    }
}
