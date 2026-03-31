package com.shipflow.orderservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderReadModelJpaRepository extends JpaRepository<OrderReadModelJpaEntity, UUID> {
}
