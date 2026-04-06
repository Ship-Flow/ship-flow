package com.shipflow.companyservice.application.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record VendorInfoResponse(
	@NonNull UUID receiverCompanyId,
	@NotBlank String receiverCompanyName,
	@NonNull UUID departureCompanyHubId
) {
}
