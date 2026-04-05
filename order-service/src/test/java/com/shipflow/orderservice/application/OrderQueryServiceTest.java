package com.shipflow.orderservice.application;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import com.shipflow.orderservice.fixture.OrderFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderReadModelRepository orderReadModelRepository;
    @InjectMocks OrderQueryService orderQueryService;

    private final UUID orderId = OrderFixture.ORDER_ID;

    @Test
    void getOrder_성공_OrderResult반환() {
        Order order = OrderFixture.order(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResult result = orderQueryService.getOrder(orderId);

        assertThat(result.id()).isEqualTo(orderId);
        assertThat(result.quantity()).isEqualTo(10);
    }

    @Test
    void getOrder_없는ID_예외발생() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getOrders_전체목록반환() {
        Order order1 = OrderFixture.order(orderId);
        Order order2 = OrderFixture.order(UUID.randomUUID());
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<OrderResult> results = orderQueryService.getOrders();

        assertThat(results).hasSize(2);
    }

    @Test
    void getReadModel_없는ID_예외발생() {
        when(orderReadModelRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderQueryService.getReadModel(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
