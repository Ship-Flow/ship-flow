package com.shipflow.shipmentservice.infrastructure.persistence;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedSagaEventRepository extends JpaRepository<ProcessedSagaEventJpaEntity, String> {
	void deleteByProcessedAtBefore(LocalDateTime cutoff);
}
