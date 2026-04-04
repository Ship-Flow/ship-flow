package com.shipflow.orderservice.integration;

import com.shipflow.config.message.RabbitMqConfig;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.model.ShipmentStatus;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import com.shipflow.orderservice.fixture.OrderFixture;
import com.shipflow.orderservice.infrastructure.messaging.event.consume.*;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class OrderMessageFlowTest extends AbstractIntegrationTest {

    @Autowired RabbitTemplate rabbitTemplate;
    @Autowired OrderRepository orderRepository;
    @Autowired OrderReadModelRepository orderReadModelRepository;

    @Test
    void 재고차감_메시지_수신시_주문상태가_CREATED로_변경된다() {
        UUID orderId = UUID.randomUUID();
        saveCreatingOrder(orderId);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE, "product.stock.decreased",
                new ProductStockDecreasedEvent(orderId, "테스트상품")
        );

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Order order = orderRepository.findById(orderId).orElseThrow();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        });
    }

    @Test
    void 재고차감실패_메시지_수신시_주문상태가_FAILED로_변경된다() {
        UUID orderId = UUID.randomUUID();
        saveCreatingOrder(orderId);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE, "product.stock.decreased.failed",
                new ProductStockDecreasedFailedEvent(orderId)
        );

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Order order = orderRepository.findById(orderId).orElseThrow();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
        });
    }

    @Test
    void 배송생성_메시지_수신시_shipmentId가_연결된다() {
        UUID orderId = UUID.randomUUID();
        UUID shipmentId = UUID.randomUUID();
        saveCreatedOrder(orderId);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE, "shipment.created",
                new ShipmentCreatedEvent(
                        orderId, shipmentId, ShipmentStatus.WAITING_AT_HUB,
                        OrderFixture.DEP_HUB_ID, "서울 허브",
                        OrderFixture.ARR_HUB_ID, "부산 허브"
                )
        );

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Order order = orderRepository.findById(orderId).orElseThrow();
            assertThat(order.getShipmentId()).isEqualTo(shipmentId);
        });
    }

    @Test
    void 배송생성실패_메시지_수신시_주문상태가_FAILED로_변경된다() {
        UUID orderId = UUID.randomUUID();
        saveCreatedOrder(orderId);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE, "shipment.creation.failed",
                new ShipmentCreationFailedEvent(orderId)
        );

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Order order = orderRepository.findById(orderId).orElseThrow();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
        });
    }

    @Test
    void 배송완료_메시지_수신시_주문상태가_COMPLETED로_변경된다() {
        UUID orderId = UUID.randomUUID();
        saveCreatedOrder(orderId);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE, "shipment.completed",
                new ShipmentCompletedEvent(orderId)
        );

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Order order = orderRepository.findById(orderId).orElseThrow();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        });
    }

    private void saveCreatingOrder(UUID orderId) {
        orderRepository.save(OrderFixture.order(orderId));
        orderReadModelRepository.save(OrderFixture.orderReadModel(orderId));
    }

    private void saveCreatedOrder(UUID orderId) {
        orderRepository.save(OrderFixture.createdOrder(orderId));
        orderReadModelRepository.save(OrderFixture.createdOrderReadModel(orderId));
    }
}
