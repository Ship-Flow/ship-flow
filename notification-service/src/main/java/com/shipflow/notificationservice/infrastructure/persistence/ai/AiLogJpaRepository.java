package com.shipflow.notificationservice.infrastructure.persistence.ai;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.notificationservice.domain.ai.AiLog;

public interface AiLogJpaRepository extends JpaRepository<AiLog, UUID> {

	Optional<AiLog> findByIdAndDeletedAtIsNull(UUID id);
}
