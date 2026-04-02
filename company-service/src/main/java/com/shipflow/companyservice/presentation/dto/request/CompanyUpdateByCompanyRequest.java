package com.shipflow.companyservice.presentation.dto.request;

public record CompanyUpdateByCompanyRequest(
	String name,
	String address
) {
}
