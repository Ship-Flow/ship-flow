package com.shipflow.companyservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.common.exception.CommonErrorCode;
import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.application.mapper.CompanyMapper;
import com.shipflow.companyservice.domain.Company;
import com.shipflow.companyservice.domain.repository.CompanyRepository;
import com.shipflow.companyservice.infrastructure.persistence.UserContext;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByAdminRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByCompanyRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForAdminResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForCompanyResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyListResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final CompanyMapper mapper;

	//external
	public CompanyCreateResponse createCompany(CompanyCreateRequest request) {
		UUID createrId = UserContext.getUserId();
		Company newCompany = Company.create(
			request.name(), request.type(), request.hubId(),
			request.address(), request.managerId(), request.name(), createrId);
		companyRepository.save(newCompany);
		return mapper.toCreateResponse(newCompany);
	}

	public void deleteCompany(UUID companyId) {
		UUID deleterId = UserContext.getUserId();
		Company company = findCompanyById(companyId);
		company.delete(deleterId);
		companyRepository.save(company);
	}

	public CompanyUpdateResponse updateByCompany(CompanyUpdateByCompanyRequest request) {
		UUID updaterId = UserContext.getUserId();
		Company company = findCompanyById(updaterId);
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}

	public CompanyUpdateResponse updateByAdmin(UUID companyId, CompanyUpdateByAdminRequest request) {
		UUID updaterId = UserContext.getUserId();
		Company company = findCompanyById(companyId);
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}

	public CompanyInfoForCompanyResponse getCompanyInfoForCompany() {
		UUID managerId = UserContext.getUserId();
		Company company = findCompanyByManagerId(managerId);
		return mapper.toCompanyInfoForCompany(company);
	}

	public CompanyInfoForAdminResponse getCompanyInfoForAdmin(UUID companyId) {
		Company company = findCompanyById(companyId);
		return mapper.toCompanyInfoForAdmin(company);
	}

	public Slice<CompanyListResponse> getCompanies(Pageable pageable) {
		Slice<Company> companies = companyRepository.findAll(pageable);
		return companies.map(mapper::toCompanyListResponse);
	}

	//internal
	public VendorInfoResponse getVendorInfo(UUID companyId) {
		Company company = findCompanyById(companyId);
		return mapper.toVendorInfoResponse(company);
	}

	//util
	private Company findCompanyById(UUID companyId) {
		return companyRepository.findById(companyId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.VALIDATION_ERROR, "해당 업체를 찾을 수 없습니다."));
	}

	private Company findCompanyByManagerId(UUID managerId) {
		return companyRepository.findByManagerId(managerId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.VALIDATION_ERROR, "해당 업체를 찾을 수 없습니다."));
	}
}
