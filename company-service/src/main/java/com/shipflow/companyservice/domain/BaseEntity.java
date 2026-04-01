package com.shipflow.companyservice.domain;

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

	public void create(UUID id) {
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
