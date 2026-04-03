package com.shipflow.productservice.presentation.dto.request;

import java.math.BigDecimal;

import com.shipflow.productservice.domain.model.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
	@NotBlank String name,
	@NotNull BigDecimal price,
	@NotNull Integer stock,
	ProductStatus status
) {
}
