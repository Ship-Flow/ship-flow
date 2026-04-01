package com.shipflow.companyservice.presentation.dto.response;

import java.util.UUID;

import com.shipflow.companyservice.domain.CompanyType;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record CompanyInfoForCompanyResponse(
	@NonNull UUID companyId,
	@NotBlank String companyName,
	@NonNull CompanyType type,
	@NonNull UUID hubId,
	@NotBlank String address,
	@NotBlank String managerName
) {
}
