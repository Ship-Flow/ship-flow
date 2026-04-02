package com.shipflow.orderservice.application;

import com.shipflow.common.messaging.publisher.EventPublisher;
import com.shipflow.orderservice.application.dto.CancelOrderCommand;
import com.shipflow.orderservice.application.dto.CreateOrderCommand;
import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.dto.UpdateOrderCommand;
import com.shipflow.orderservice.application.service.OrderCommandService;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import com.shipflow.orderservice.fixture.OrderFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock OrderRepository orderRepository;                  // 가짜 객체 1
    @Mock EventPublisher eventPublisher;                    // 가짜 객체 2
    @InjectMocks OrderCommandService orderCommandService;   // orderCommandService 에 위 가짝 객체 2개를 주입해줌

    private final UUID orderId = OrderFixture.ORDER_ID;
    private final UUID userId  = OrderFixture.USER_ID;

    // ─────────────────────────────────────────────
    // createOrder
    // ─────────────────────────────────────────────

    @Test
    void createOrder_성공_저장후이벤트발행() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.save(any(Order.class))).thenReturn(order); // 가짜 객체 save 가 실행되면 실제로 실행하지 않고 order를 return 받는 과정

        CreateOrderCommand cmd = new CreateOrderCommand(
                OrderFixture.USER_ID, OrderFixture.PRODUCT_ID,
                OrderFixture.SUPPLIER_ID, OrderFixture.RECEIVER_ID,
                OrderFixture.DEP_HUB_ID, OrderFixture.ARR_HUB_ID,
                10, OrderFixture.DEADLINE, "테스트 메모"
        );

        OrderResult result = orderCommandService.createOrder(cmd, userId);

        assertThat(result.status()).isEqualTo(OrderStatus.CREATING);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any());
    }

    // ─────────────────────────────────────────────
    // confirmCreation
    // ─────────────────────────────────────────────

    @Test
    void confirmCreation_CREATING상태_CREATED로전이() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderCommandService.confirmCreation(orderId, "상품명");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        verify(eventPublisher).publish(any());
    }

    @Test
    void confirmCreation_없는ID_예외발생() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderCommandService.confirmCreation(orderId, "상품명"))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // failOrderByShipment
    // ─────────────────────────────────────────────

    @Test
    void failOrderByShipment_CREATING상태_FAILED로전이_이벤트발행() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderCommandService.failOrderByShipment(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
        verify(eventPublisher).publish(any());
    }

    // ─────────────────────────────────────────────
    // cancelOrder
    // ─────────────────────────────────────────────

    @Test
    void cancelOrder_CREATING상태_CANCELED로전이_이벤트발행() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderCommandService.cancelOrder(orderId, new CancelOrderCommand("재고 부족"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.getCancelReason()).isEqualTo("재고 부족");
        verify(eventPublisher).publish(any());
    }

    // ─────────────────────────────────────────────
    // completeOrder
    // ─────────────────────────────────────────────

    @Test
    void completeOrder_CREATED상태_COMPLETED로전이() {
        Order order = OrderFixture.createdOrder(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderCommandService.completeOrder(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    // ─────────────────────────────────────────────
    // updateOrder
    // ─────────────────────────────────────────────

    @Test
    void updateOrder_CREATING상태_수량변경() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        UpdateOrderCommand cmd = new UpdateOrderCommand(
                OrderFixture.PRODUCT_ID,
                OrderFixture.SUPPLIER_ID, OrderFixture.RECEIVER_ID,
                OrderFixture.DEP_HUB_ID, OrderFixture.ARR_HUB_ID,
                20, OrderFixture.DEADLINE, "수정된 메모"
        );

        OrderResult result = orderCommandService.updateOrder(orderId, cmd, userId);

        assertThat(result.quantity()).isEqualTo(20);
    }

    // ─────────────────────────────────────────────
    // deleteOrder
    // ─────────────────────────────────────────────

    @Test
    void deleteOrder_소프트삭제_deletedAt설정() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderCommandService.deleteOrder(orderId, userId);

        assertThat(order.isDeleted()).isTrue();
        verify(orderRepository).save(order);
    }
}
