package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderJpaEntity::toDomain);
    }

    /**
     * Order -> OrderJpaEntity 로 변환해서 저장
     * */
    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(OrderJpaEntity.from(order));
        return saved.toDomain();
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderJpaEntity::toDomain)
                .toList();
    }
}
