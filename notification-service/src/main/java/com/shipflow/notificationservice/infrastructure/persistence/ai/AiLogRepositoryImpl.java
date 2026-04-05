package com.shipflow.notificationservice.infrastructure.persistence.ai;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.repository.AiLogRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiLogRepositoryImpl implements AiLogRepository {

	private final AiLogJpaRepository aiLogJpaRepository;

	@Override
	public AiLog save(AiLog aiLog) {
		return aiLogJpaRepository.save(aiLog);
	}

	@Override
	public Optional<AiLog> findByIdAndDeletedAtIsNull(UUID aiId) {
		return aiLogJpaRepository.findByIdAndDeletedAtIsNull(aiId);
	}

	@Override
	public Page<AiLog> findAllByDeletedAtIsNull(Pageable pageable) {
		return aiLogJpaRepository.findAllByDeletedAtIsNull(pageable);
	}
}

