package com.shipflow.productservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;
import lombok.Getter;

@Getter
public class BaseEntity {
	protected LocalDateTime createdAt;
	protected UUID createdBy;
	protected LocalDateTime updatedAt;
	protected UUID updatedBy;
	protected LocalDateTime deletedAt;
	protected UUID deletedBy;


	public void delete(UUID id) {
		Objects.requireNonNull(id, "deletedBy id는 필수입니다.");
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = id;
	}
}