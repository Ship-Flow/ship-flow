package com.shipflow.companyservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.common.exception.CommonErrorCode;
import com.shipflow.companyservice.application.client.ProductFeignClient;
import com.shipflow.companyservice.application.client.UserFeignClient;
import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.application.mapper.CompanyMapper;
import com.shipflow.companyservice.domain.exception.CompanyErrorCode;
import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.domain.repository.CompanyRepository;
import com.shipflow.companyservice.infrastructure.context.UserContext;
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
	private final ProductFeignClient productFeignClient;

	//external
	@Transactional
	public CompanyCreateResponse createCompany(CompanyCreateRequest request) {
		UUID createrId = getUserId();
		String managerName = userFeignClient.getUserInfoById(request.managerId()).name();

		Company newCompany = Company.create(
			request.name(), request.type(), request.hubId(),
			request.address(), request.managerId(), managerName, createrId);

		companyRepository.save(newCompany);

		userFeignClient.updateCompanyManager(request.managerId());

		return mapper.toCreateResponse(newCompany);
	}

	@Transactional
	public void deleteCompany(UUID companyId) {
		UUID deleterId = getUserId();
		Company company = findCompanyById(companyId);
		company.delete(deleterId);
		companyRepository.save(company);
		productFeignClient.deleteProductByCompanyId(companyId);
	}

	@Transactional
	public CompanyUpdateResponse updateByCompany(CompanyUpdateByCompanyRequest request) {
		UUID updaterId = getUserId();
		Company company = findCompanyByManagerId(updaterId);
		company.updateByCompany(request.name(), request.address(), updaterId);
		companyRepository.save(company);
		return mapper.toUpdateResponse(company);
	}

	@Transactional
	public CompanyUpdateResponse updateByAdmin(UUID companyId, CompanyUpdateByAdminRequest request) {
		UUID updaterId = getUserId();
		Company company = findCompanyById(companyId);

		UUID requestedManagerId = request.managerId();
		UUID targetManagerId;
		String managerName;

		boolean isManagerChanged = false;

		if (requestedManagerId != null && !requestedManagerId.equals(company.getManagerId())) {
			// 담당자의 변경이 있는 경우에만 user 조회 요청
			targetManagerId = requestedManagerId;
			managerName = userFeignClient.getUserInfoById(requestedManagerId).name();
			isManagerChanged = true;
		} else {
			// 담당자 변경이 없을 시 현재 담당자 유지
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

		if (isManagerChanged)
			userFeignClient.updateCompanyManager(request.managerId());

		return mapper.toUpdateResponse(company);
	}

	public CompanyInfoForCompanyResponse getCompanyInfoForCompany() {
		UUID managerId = getUserId();
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

	@Transactional
	public void deleteProductsByHub(UUID hubId) {
		List<Company> companies = companyRepository.findAllByHubId(hubId);
		List<UUID> companyIds = companies.stream().map(Company::getId).toList();
		companies.forEach(company -> {
			company.delete(UserContext.getUserId());
			companyRepository.save(company);
		});
		productFeignClient.deleteProductsByCompanyIds(companyIds);
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

	private UUID getUserId() {
		UUID userId = UserContext.getUserId();
		if (userId == null)
			throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
		return userId;
	}
}
