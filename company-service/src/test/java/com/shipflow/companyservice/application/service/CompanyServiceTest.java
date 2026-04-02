package com.shipflow.companyservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockHttpServletRequest;

import com.shipflow.companyservice.application.client.UserFeignClient;
import com.shipflow.companyservice.application.dto.response.UserInfoResponse;
import com.shipflow.companyservice.application.mapper.CompanyMapper;
import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.domain.model.CompanyType;
import com.shipflow.companyservice.domain.repository.CompanyRepository;
import com.shipflow.companyservice.fixture.CompanyFixture;
import com.shipflow.companyservice.infrastructure.web.UserContext;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByAdminRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByCompanyRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForAdminResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForCompanyResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyListResponse;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

	@Mock
	private CompanyRepository companyRepository;
	@Spy
	private CompanyMapper mapper = Mappers.getMapper(CompanyMapper.class);
	@Mock
	private UserFeignClient userFeignClient;
	@InjectMocks
	private CompanyService companyService;

	@Captor
	private ArgumentCaptor<Company> companyCaptor;

	@AfterEach
	void tearDown() {
		UserContext.clear();
	}

	@Test
	void createCompany_success() {
		//given
		setHttpHeaders(UUID.randomUUID().toString(), "Master");
		UUID createrId = UUID.randomUUID();
		Company company = CompanyFixture.create();
		CompanyCreateRequest request = new CompanyCreateRequest(company.getName(), company.getType(),
			company.getHubId(), company.getAddress(), createrId);
		UserInfoResponse userInfo = new UserInfoResponse(request.managerId(), "testManagerName");
		given(userFeignClient.getUserNameById(request.managerId())).willReturn(userInfo);

		//when
		companyService.createCompany(request);

		//then
		verify(companyRepository).save(companyCaptor.capture());
		Company savedCompany = companyCaptor.getValue();
		assertThat(savedCompany.getName()).isEqualTo(request.name());
		assertThat(savedCompany.getType()).isEqualTo(request.type());
		assertThat(savedCompany.getHubId()).isEqualTo(request.hubId());
		assertThat(savedCompany.getManagerId()).isEqualTo(request.managerId());
		assertThat(savedCompany.getAddress()).isEqualTo(request.address());
	}

	@Test
	void deleteCompany_success() {
		//given
		UUID companyId = UUID.randomUUID();
		Company company = CompanyFixture.create();
		given(companyRepository.findById(companyId)).willReturn(Optional.of(company));

		//when
		companyService.deleteCompany(companyId);

		//then
		verify(companyRepository).save(companyCaptor.capture());
		Company savedCompany = companyCaptor.getValue();
		assertThat(savedCompany.isDeleted()).isTrue();
	}

	@Test
	void updateByCompany_success() {
		//given
		setHttpHeaders(UUID.randomUUID().toString(), "Company_Manager");

		Company company = CompanyFixture.create();
		CompanyUpdateByCompanyRequest request = new CompanyUpdateByCompanyRequest("testName", "testAddress");
		given(companyRepository.findByManagerId(any())).willReturn(Optional.of(company));

		//when
		companyService.updateByCompany(request);

		//then
		verify(companyRepository).save(companyCaptor.capture());
		Company savedCompany = companyCaptor.getValue();
		assertThat(savedCompany.getName()).isEqualTo(request.name());
		assertThat(savedCompany.getAddress()).isEqualTo(request.address());
	}

	@Test
	void updateByAdmin_success() {
		//given
		Company company = CompanyFixture.create();
		CompanyUpdateByAdminRequest request = new CompanyUpdateByAdminRequest(
			"testName", CompanyType.Receiver, UUID.randomUUID(), "testAddress", UUID.randomUUID());
		UserInfoResponse userInfo = new UserInfoResponse(request.managerId(), "testManagerName");
		given(userFeignClient.getUserNameById(request.managerId())).willReturn(userInfo);
		given(companyRepository.findById(any())).willReturn(Optional.of(company));

		//when
		companyService.updateByAdmin(company.getId(), request);

		//then
		verify(companyRepository).save(companyCaptor.capture());
		Company savedCompany = companyCaptor.getValue();
		assertThat(savedCompany.getName()).isEqualTo(request.name());
		assertThat(savedCompany.getType()).isEqualTo(request.type());
		assertThat(savedCompany.getManagerId()).isEqualTo(request.managerId());
		assertThat(savedCompany.getAddress()).isEqualTo(request.address());
	}

	@Test
	void getCompanyInfoForCompany_success() {
		//given
		setHttpHeaders(UUID.randomUUID().toString(), "Company_Manager");
		Company company = CompanyFixture.create();
		given(companyRepository.findByManagerId(UserContext.getUserId())).willReturn(Optional.of(company));

		//when
		CompanyInfoForCompanyResponse companyInfo = companyService.getCompanyInfoForCompany();

		//then
		assertThat(companyInfo.id()).isEqualTo(company.getId());
		assertThat(companyInfo.name()).isEqualTo(company.getName());
		assertThat(companyInfo.type()).isEqualTo(company.getType());
		assertThat(companyInfo.managerName()).isEqualTo(company.getManagerName());
		assertThat(companyInfo.address()).isEqualTo(company.getAddress());

	}

	@Test
	void getCompanyInfoForAdmin_success() {
		//given
		UUID companyId = UUID.randomUUID();
		Company company = CompanyFixture.create();
		given(companyRepository.findById(companyId)).willReturn(Optional.of(company));

		//when
		CompanyInfoForAdminResponse companyInfo = companyService.getCompanyInfoForAdmin(companyId);

		//then
		assertThat(companyInfo.id()).isEqualTo(company.getId());
		assertThat(companyInfo.name()).isEqualTo(company.getName());
		assertThat(companyInfo.type()).isEqualTo(company.getType());
		assertThat(companyInfo.managerName()).isEqualTo(company.getManagerName());
		assertThat(companyInfo.address()).isEqualTo(company.getAddress());
	}

	@Test
	void getCompanies_success() {
		//given
		Company company = CompanyFixture.create();
		List<Company> companies = List.of(company);
		Pageable pageable = Pageable.ofSize(10);
		Slice<Company> slice = new SliceImpl<>(companies, pageable, false);
		given(companyRepository.findAll(pageable)).willReturn(slice);
		given(companyRepository.findById(company.getId())).willReturn(Optional.of(company));

		//when
		Slice<CompanyListResponse> response = companyService.getCompanies(pageable);

		//then
		assertThat(response.getContent().size()).isEqualTo(1);
		assertThat(response.hasNext()).isFalse();
	}

	private void setHttpHeaders(String userId, String role) {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.addHeader("X-User-Id", userId);
		httpRequest.addHeader("X-User-Role", role);
		UserContext.setUserContext(httpRequest);
	}
}