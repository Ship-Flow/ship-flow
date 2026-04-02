package com.shipflow.productservice.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.productservice.domain.model.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record ProductInfoResponse(
	@NonNull UUID id,
	@NotBlank String name,
	@NonNull BigDecimal price,
	@NonNull Integer stock,
	@NonNull ProductStatus status, // Enum 타입 가정
	@NonNull UUID companyId,
	@NotBlank String companyName,
	@NonNull UUID hubId,
	@NonNull Boolean isHide,
	@NonNull LocalDateTime createdAt,
	@NonNull UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy,
	LocalDateTime deletedAt,
	UUID deletedBy
) {
}
