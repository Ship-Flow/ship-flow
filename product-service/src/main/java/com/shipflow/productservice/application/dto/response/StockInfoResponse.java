package com.shipflow.productservice.application.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;

public record StockInfoResponse(
	@NonNull UUID productId,
	@NonNull @Positive Integer stock
){
}
