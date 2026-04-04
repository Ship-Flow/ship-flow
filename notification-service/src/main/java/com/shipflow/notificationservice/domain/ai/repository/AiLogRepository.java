package com.shipflow.notificationservice.domain.ai.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shipflow.notificationservice.domain.ai.AiLog;

public interface AiLogRepository {

	AiLog save(AiLog aiLog);

	Optional<AiLog> findByIdAndDeletedAtIsNull(UUID id);

	Page<AiLog> findAllByDeletedAtIsNull(Pageable pageable);
}