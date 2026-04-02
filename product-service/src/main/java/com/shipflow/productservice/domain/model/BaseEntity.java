package com.shipflow.productservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.common.exception.CommonErrorCode;

import lombok.Getter;

@Getter
public class BaseEntity {
	protected LocalDateTime createdAt;
	protected UUID createdBy;
	protected LocalDateTime updatedAt;
	protected UUID updatedBy;
	protected LocalDateTime deletedAt;
	protected UUID deletedBy;

	public void create(UUID id) {
		if (id == null)
			throw new BusinessException(CommonErrorCode.VALIDATION_ERROR);
		this.createdAt = LocalDateTime.now();
		this.createdBy = id;
	}

	public void update(UUID id) {
		this.updatedAt = LocalDateTime.now();
		this.updatedBy = id;
	}

	public void delete(UUID id) {
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = id;
	}
}