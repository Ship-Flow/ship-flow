package com.shipflow.productservice.application.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

public record StockInfoResponse(
	@NonNull UUID productId,
	@NotBlank String productName,
	@NonNull UUID supplierCompanyId,
	@NotBlank String supplierCompanyName,
	@NonNull UUID departureHubId,
	@NonNull @Positive Integer stock
){
}
