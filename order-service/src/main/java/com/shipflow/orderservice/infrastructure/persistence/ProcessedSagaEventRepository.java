package com.shipflow.orderservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ProcessedSagaEventRepository extends JpaRepository<ProcessedSagaEventJpaEntity, String> {
    void deleteByProcessedAtBefore(LocalDateTime cutoff);
}
