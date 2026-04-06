package com.shipflow.companyservice.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForAdminResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForCompanyResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyListResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

	//Entity -> DTO
	CompanyCreateResponse toCreateResponse(Company company);

	CompanyUpdateResponse toUpdateResponse(Company company);

	@Mapping(target = "id", source = "receiverCompanyId")
	@Mapping(target = "name", source = "receiverCompanyName")
	@Mapping(target = "hubId", source = "departureCompanyHubId")
	VendorInfoResponse toVendorInfoResponse(Company company);

	CompanyInfoForCompanyResponse toCompanyInfoForCompany(Company company);

	CompanyInfoForAdminResponse toCompanyInfoForAdmin(Company company);

	CompanyListResponse toCompanyListResponse(Company company);
}
