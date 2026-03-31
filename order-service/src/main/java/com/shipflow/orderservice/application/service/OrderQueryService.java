package com.shipflow.orderservice.application.service;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.domain.exception.OrderNotFoundException;
import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderReadModelRepository orderReadModelRepository;

    public OrderResult getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        return OrderResult.from(order);
    }

    public List<OrderResult> getOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResult::from)
                .toList();
    }

    public OrderReadModel getReadModel(UUID orderId) {
        return orderReadModelRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("주문 읽기 모델을 찾을 수 없습니다: " + orderId));
    }
}
