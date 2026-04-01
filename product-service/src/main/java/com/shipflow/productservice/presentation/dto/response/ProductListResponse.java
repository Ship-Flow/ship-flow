package com.shipflow.productservice.presentation.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record ProductListResponse(
	@NonNull UUID productId,
	@NotBlank String productName
) {
}
