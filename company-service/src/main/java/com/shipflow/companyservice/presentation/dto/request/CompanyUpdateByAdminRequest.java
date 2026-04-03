package com.shipflow.companyservice.presentation.dto.request;

import java.util.UUID;

import com.shipflow.companyservice.domain.model.CompanyType;

public record CompanyUpdateByAdminRequest(
	String name,
	CompanyType type,
	UUID hubId,
	String address,
	UUID managerId
) {
}
