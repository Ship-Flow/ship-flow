package com.shipflow.orderservice.application;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.application.dto.OrderSearchCondition;
import com.shipflow.orderservice.application.service.OrderQueryService;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import com.shipflow.orderservice.fixture.OrderFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    // ─────────────────────────────────────
    // searchOrders 테스트
    // ─────────────────────────────────────

    @Test
    void searchOrders_빈조건_pageSize10_정상반환() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<OrderReadModel> fakeSlice = new SliceImpl<>(
                List.of(OrderFixture.orderReadModel(orderId)), pageable, false
        );
        when(orderReadModelRepository.search(any(), any())).thenReturn(fakeSlice);

        Slice<OrderReadModel> result = orderQueryService.searchOrders(condition, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getOrderId()).isEqualTo(orderId);
    }

    @Test
    void searchOrders_허용되지않은pageSize25_10으로정규화() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.Direction.DESC, "createdAt"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(orderReadModelRepository.search(any(), captor.capture()))
                .thenReturn(new SliceImpl<>(List.of(), PageRequest.of(0, 10), false));

        orderQueryService.searchOrders(condition, pageable);

        assertThat(captor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void searchOrders_허용된pageSize30_그대로전달() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "createdAt"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(orderReadModelRepository.search(any(), captor.capture()))
                .thenReturn(new SliceImpl<>(List.of(), pageable, false));

        orderQueryService.searchOrders(condition, pageable);

        assertThat(captor.getValue().getPageSize()).isEqualTo(30);
    }

    @Test
    void searchOrders_허용된pageSize50_그대로전달() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(orderReadModelRepository.search(any(), captor.capture()))
                .thenReturn(new SliceImpl<>(List.of(), pageable, false));

        orderQueryService.searchOrders(condition, pageable);

        assertThat(captor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void searchOrders_유효하지않은정렬필드_createdAtDESC폴백() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "productId"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(orderReadModelRepository.search(any(), captor.capture()))
                .thenReturn(new SliceImpl<>(List.of(), PageRequest.of(0, 10), false));

        orderQueryService.searchOrders(condition, pageable);

        Sort.Order captured = captor.getValue().getSort().iterator().next();
        assertThat(captured.getProperty()).isEqualTo("createdAt");
        assertThat(captured.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void searchOrders_유효한정렬updatedAtASC_그대로전달() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "updatedAt"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(orderReadModelRepository.search(any(), captor.capture()))
                .thenReturn(new SliceImpl<>(List.of(), pageable, false));

        orderQueryService.searchOrders(condition, pageable);

        Sort.Order captured = captor.getValue().getSort().iterator().next();
        assertThat(captured.getProperty()).isEqualTo("updatedAt");
        assertThat(captured.getDirection()).isEqualTo(Sort.Direction.ASC);
    }
}
