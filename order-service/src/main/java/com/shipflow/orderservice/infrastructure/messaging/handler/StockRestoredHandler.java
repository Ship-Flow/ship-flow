package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.orderservice.infrastructure.messaging.config.OrderRabbitConfig;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.ProductStockRestoredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockRestoredHandler extends IdempotentSagaHandler<ProductStockRestoredEvent> {

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_STOCK_RESTORED)
    public void receive(ProductStockRestoredEvent event) {
        handle(event);
    }

    @Override
    protected void doProcess(ProductStockRestoredEvent event) {
        // 재고 복원 완료 알림 수신. 주문은 이미 CANCELED 상태이므로 추가 처리 없음.
    }
}
