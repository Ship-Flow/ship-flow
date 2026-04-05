package com.shipflow.orderservice.domain.repository;

import com.shipflow.orderservice.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID id);
    Order save(Order order);
    List<Order> findAll();
}
