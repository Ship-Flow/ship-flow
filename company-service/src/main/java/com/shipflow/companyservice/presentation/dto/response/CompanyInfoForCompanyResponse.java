package com.shipflow.companyservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.companyservice.domain.model.CompanyType;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record CompanyInfoForCompanyResponse(
	@NonNull UUID id,
	@NotBlank String name,
	@NonNull CompanyType type,
	@NonNull UUID hubId,
	@NotBlank String address,
	@NotBlank String managerName
) {
}
