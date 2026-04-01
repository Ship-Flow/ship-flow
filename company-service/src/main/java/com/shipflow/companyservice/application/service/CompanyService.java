package com.shipflow.companyservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.application.mapper.CompanyMapper;
import com.shipflow.companyservice.domain.Company;
import com.shipflow.companyservice.domain.repository.CompanyRepository;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByAdminRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByCompanyRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final CompanyMapper mapper;

	public CompanyCreateResponse createCompany(CompanyCreateRequest request, UUID createrId) {
		Company newCompany = Company.create(
			request.name(), request.type(), request.hubId(),
			request.address(), request.managerId(), request.name(), createrId);
		companyRepository.save(newCompany);
		return mapper.toCreateResponse(newCompany);
	}

	public void deleteCompany(UUID companyId, UUID deleterId) {
		Company company = findCompanyById(companyId);
		company.delete(deleterId);
		companyRepository.save(company);
	}

	public CompanyUpdateResponse updateByCompany(CompanyUpdateByCompanyRequest request,
		UUID updaterId) {
		Company company = companyRepository.findByManagerId(updaterId)
			.orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}

	public CompanyUpdateResponse updateByAdmin(UUID companyId, CompanyUpdateByAdminRequest request, UUID updaterId) {
		Company company = findCompanyById(companyId);
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}

	public VendorInfoResponse getVendorInfo(UUID companyId) {
		Company company = findCompanyById(companyId);
		return mapper.toVendorInfoResponse(company);
	}

	private Company findCompanyById(UUID companyId) {
		return companyRepository.findById(companyId)
			.orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
	}
}
