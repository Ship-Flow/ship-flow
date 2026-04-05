package com.shipflow.orderservice.domain.repository;

import com.shipflow.orderservice.domain.model.OrderReadModel;

import java.util.Optional;
import java.util.UUID;

public interface OrderReadModelRepository {
    Optional<OrderReadModel> findById(UUID orderId);
    OrderReadModel save(OrderReadModel readModel);
}
