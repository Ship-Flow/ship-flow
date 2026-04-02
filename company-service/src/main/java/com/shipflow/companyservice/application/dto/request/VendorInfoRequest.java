package com.shipflow.companyservice.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record VendorInfoRequest(
	@NonNull UUID id,
	@NotBlank String name,
	@NonNull UUID hubId
) {
}
