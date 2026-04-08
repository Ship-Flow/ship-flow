package com.shipflow.productservice.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.productservice.domain.model.ProductStatus;

public record ProductCreateResponse(
	UUID id, String name, String description, BigDecimal price, Integer stock,
	ProductStatus status, UUID companyId, String companyName,
	UUID hubId, Boolean isHide, LocalDateTime createdAt
) {
}
