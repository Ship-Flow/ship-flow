package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.infrastructure.messaging.config.OrderRabbitConfig;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.ProductStockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreasedHandler extends IdempotentSagaHandler<ProductStockDecreasedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_STOCK_DECREASED)
    public void receive(ProductStockDecreasedEvent event) {
        handle(event);
    }

    @Override
    protected void doProcess(ProductStockDecreasedEvent event) {
        orderCommandService.confirmCreation(event.getOrderId(), event.getProductName());
    }
}
