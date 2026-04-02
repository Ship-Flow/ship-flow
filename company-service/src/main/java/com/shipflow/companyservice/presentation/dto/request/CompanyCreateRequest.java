package com.shipflow.companyservice.presentation.dto.request;

import java.util.UUID;

import com.shipflow.companyservice.domain.model.CompanyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyCreateRequest(
	@NotBlank String name,
	@NotNull CompanyType type,
	@NotNull UUID hubId,
	@NotBlank String address,
	@NotNull UUID managerId
) {
}
