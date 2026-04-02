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

    /**
     * 주문 도메인 엔티티를 조회합니다.
     * 상태 변경 전 체크나 정규화된 최신 원본 데이터가 필요한 관리자 기능에서 사용합니다.
     */
    public OrderResult getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResult.from(order);
    }

    /**
     * 전체 주문 목록을 조회합니다.
     * 데이터 양이 적거나, 시스템 내부의 배치 작업/동기화 작업 시 사용합니다.
     */
    public List<OrderResult> getOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResult::from)
                .toList();
    }

    /**
     * 읽기 최적화된 주문 모델을 조회합니다.
     * JOIN 없이 배송 정보를 포함한 데이터를 한 번에 가져옵니다.
     */
    public OrderReadModel getReadModel(UUID orderId) {
        return orderReadModelRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
