package com.shipflow.companyservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.companyservice.application.client.UserFeignClient;
import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.application.mapper.CompanyMapper;
import com.shipflow.companyservice.domain.exception.CompanyErrorCode;
import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.domain.repository.CompanyRepository;
import com.shipflow.companyservice.infrastructure.web.UserContext;
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
@Transactional(readOnly = true)
public class CompanyService {
	private final CompanyRepository companyRepository;
	private final CompanyMapper mapper;
	private final UserFeignClient userFeignClient;

	//external
	@Transactional
	public CompanyCreateResponse createCompany(CompanyCreateRequest request) {
		UUID createrId = UserContext.getUserId();
		String managerName = userFeignClient.getUserNameById(request.managerId()).name();
		Company newCompany = Company.create(
			request.name(), request.type(), request.hubId(),
			request.address(), request.managerId(), managerName, createrId);
		Company savedCompany = companyRepository.save(newCompany);
		return mapper.toCreateResponse(savedCompany);
	}

	@Transactional
	public void deleteCompany(UUID companyId) {
		UUID deleterId = UserContext.getUserId();
		Company company = findCompanyById(companyId);
		company.delete(deleterId);
		companyRepository.save(company);
	}

	@Transactional
	public CompanyUpdateResponse updateByCompany(CompanyUpdateByCompanyRequest request) {
		UUID updaterId = UserContext.getUserId();
		Company company = findCompanyByManagerId(updaterId);
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}

	@Transactional
	public CompanyUpdateResponse updateByAdmin(UUID companyId, CompanyUpdateByAdminRequest request) {
		UUID updaterId = UserContext.getUserId();
		Company company = findCompanyById(companyId);

		UUID requestedManagerId = request.managerId();
		UUID targetManagerId;
		String managerName;

		if (requestedManagerId != null && !requestedManagerId.equals(company.getManagerId())) {
			// Manager change requested: lookup new manager name via Feign
			targetManagerId = requestedManagerId;
			managerName = userFeignClient.getUserNameById(requestedManagerId).name();
		} else {
			// No manager change requested: keep existing manager information
			targetManagerId = company.getManagerId();
			managerName = company.getManagerName();
		}

		company.updateByAdmin(
			request.name(),
			request.type(),
			request.hubId(),
			request.address(),
			targetManagerId,
			managerName,
			updaterId
		);
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
			.orElseThrow(() -> new BusinessException(CompanyErrorCode.COMPANY_NOT_FOUND));
	}

	private Company findCompanyByManagerId(UUID managerId) {
		return companyRepository.findByManagerId(managerId)
			.orElseThrow(() -> new BusinessException(CompanyErrorCode.COMPANY_NOT_FOUND));
	}
}
