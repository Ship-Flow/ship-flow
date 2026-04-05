package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderReadModelRepositoryImpl implements OrderReadModelRepository {

    private final OrderReadModelJpaRepository jpaRepository;

    @Override
    public Optional<OrderReadModel> findById(UUID orderId) {
        return jpaRepository.findById(orderId).map(OrderReadModelJpaEntity::toDomain);
    }

    @Override
    public OrderReadModel save(OrderReadModel readModel) {
        OrderReadModelJpaEntity saved = jpaRepository.save(OrderReadModelJpaEntity.from(readModel));
        return saved.toDomain();
    }
}
