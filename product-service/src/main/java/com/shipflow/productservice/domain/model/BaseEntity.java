package com.shipflow.productservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class BaseEntity {
	LocalDateTime createdAt;
	UUID createdBy;
	LocalDateTime updatedAt;
	UUID updatedBy;
	LocalDateTime deletedAt;
	UUID deletedBy;

import java.util.Objects;

	public void create(UUID id) {
		Objects.requireNonNull(id, "createdBy id는 필수입니다.");
		this.createdAt = LocalDateTime.now();
		this.createdBy = id;
	}

	public void update(UUID id) {
		Objects.requireNonNull(id, "updatedBy id는 필수입니다.");
		this.updatedAt = LocalDateTime.now();
		this.updatedBy = id;
	}

	public void delete(UUID id) {
		Objects.requireNonNull(id, "deletedBy id는 필수입니다.");
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = id;
	}
}