package com.shipflow.companyservice.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record VendorInfoRequest(
	@NonNull UUID companyId,
	@NotBlank String companyName,
	@NonNull UUID hubId
) {
}
