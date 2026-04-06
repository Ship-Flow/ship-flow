package com.shipflow.productservice.application.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record UserInfoResponse(
	@NonNull UUID id,
	@NotBlank String name,
	@NonNull UUID hubId,
	@NonNull UUID companyId
) {
}
