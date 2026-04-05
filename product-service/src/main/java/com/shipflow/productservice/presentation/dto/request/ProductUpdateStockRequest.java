package com.shipflow.productservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductUpdateStockRequest(
	@NotNull
	@PositiveOrZero
	Integer stock
) {
}
