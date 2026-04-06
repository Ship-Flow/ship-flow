package com.shipflow.companyservice.application.dto.response;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record UserInfoResponse(
	@NonNull UUID id,
	@NotBlank String name,
	@NonNull UUID companyId,
	@NonNull UUID HubId
) {
}
