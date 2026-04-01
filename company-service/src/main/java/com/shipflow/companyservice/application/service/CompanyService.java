package com.shipflow.companyservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

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
		Company newCompany = mapper.toEntity(request, createrId);
		companyRepository.save(newCompany);
		return mapper.toCreateResponse(newCompany);
	}

	public void deleteCompany(UUID companyId, UUID deleterId) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
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
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}
}
