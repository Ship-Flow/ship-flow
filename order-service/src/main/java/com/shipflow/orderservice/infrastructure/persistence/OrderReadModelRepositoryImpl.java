package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.orderservice.application.dto.OrderSearchCondition;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.repository.OrderReadModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderReadModelRepositoryImpl implements OrderReadModelRepository {

    private final OrderReadModelJpaRepository jpaRepository;
    private final OrderReadModelQueryRepository queryRepository;

    @Override
    public Optional<OrderReadModel> findById(UUID orderId) {
        return jpaRepository.findById(orderId).map(OrderReadModelJpaEntity::toDomain);
    }

    @Override
    public OrderReadModel save(OrderReadModel readModel) {
        OrderReadModelJpaEntity saved = jpaRepository.save(OrderReadModelJpaEntity.from(readModel));
        return saved.toDomain();
    }

    @Override
    public Slice<OrderReadModel> search(OrderSearchCondition condition, Pageable pageable) {
        return queryRepository.search(condition, pageable);
    }
}
