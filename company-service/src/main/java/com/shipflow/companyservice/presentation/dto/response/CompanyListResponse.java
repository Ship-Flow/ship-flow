package com.shipflow.companyservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.companyservice.domain.CompanyType;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record CompanyListResponse(
	@NonNull UUID companyId,
	@NotBlank String companyName,
	@NonNull CompanyType type,
	@NotBlank String address
) {
}
