package com.shipflow.common.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Column(nullable = false, updatable = false)
	@CreatedDate
	protected LocalDateTime createdAt;

	@Column(nullable = false, updatable = false)
	@CreatedBy
	protected UUID createdBy;

	@Column(nullable = false)
	@LastModifiedDate
	protected LocalDateTime updatedAt;

	@Column(nullable = false)
	@LastModifiedBy
	protected UUID updatedBy;

	protected LocalDateTime deletedAt;

	protected UUID deletedBy;
	
	protected void softDelete(UUID userId) {
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = userId;
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}
}
