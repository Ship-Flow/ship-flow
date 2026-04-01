package com.shipflow.companyservice.application.mapper;

import org.mapstruct.Mapper;

import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.domain.Company;
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

	VendorInfoResponse toVendorInfoResponse(Company company);

	CompanyInfoForCompanyResponse toCompanyInfoForCompany(Company company);

	CompanyInfoForAdminResponse toCompanyInfoForAdmin(Company company);

	CompanyListResponse toCompanyListResponse(Company company);
}
