package com.shipflow.companyservice.presentation.dto.request;

import org.hibernate.validator.constraints.UUID;

import com.shipflow.companyservice.domain.CompanyType;

public record CompanyUpdateByAdminRequest(
	String name,
	CompanyType type,
	UUID hubId,
	String address,
	UUID managerId
) {
}
