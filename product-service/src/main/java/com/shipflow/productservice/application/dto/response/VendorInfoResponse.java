package com.shipflow.productservice.application.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record VendorInfoResponse(
	@NonNull UUID companyId,
	@NotBlank String companyName,
	@NonNull UUID hubId
) {
}
