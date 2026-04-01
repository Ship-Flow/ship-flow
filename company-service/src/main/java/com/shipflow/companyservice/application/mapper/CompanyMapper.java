package com.shipflow.companyservice.application.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.shipflow.companyservice.domain.Company;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
	//DTO -> Entity
	Company toEntity(CompanyCreateRequest request, UUID createdBy);

	//Entity -> DTO
	CompanyCreateResponse toCreateResponse(Company company);

	CompanyUpdateResponse toUpdateResponse(Company company);
}
