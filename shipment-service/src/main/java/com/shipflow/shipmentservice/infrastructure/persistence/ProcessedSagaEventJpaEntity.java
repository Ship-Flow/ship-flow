package com.shipflow.shipmentservice.infrastructure.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_processed_saga_events")
public class ProcessedSagaEventJpaEntity {

	@Id
	@Column(name = "event_id", length = 36, updatable = false)
	private String eventId;

	@Column(name = "event_type", length = 100, nullable = false, updatable = false)
	private String eventType;

	@Column(name = "processed_at", nullable = false, updatable = false)
	private LocalDateTime processedAt;

	public static ProcessedSagaEventJpaEntity of(String eventId, String eventType) {
		ProcessedSagaEventJpaEntity entity = new ProcessedSagaEventJpaEntity();
		entity.eventId = eventId;
		entity.eventType = eventType;
		entity.processedAt = LocalDateTime.now();
		return entity;
	}
}
