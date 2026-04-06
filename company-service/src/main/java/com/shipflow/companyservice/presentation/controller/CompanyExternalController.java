package com.shipflow.companyservice.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.companyservice.application.service.CompanyService;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByAdminRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByCompanyRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForAdminResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForCompanyResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyListResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyExternalController {
	private final CompanyService companyService;

	@PostMapping
	public ResponseEntity<ApiResponse<CompanyCreateResponse>> createCompany(
		@RequestBody @Valid CompanyCreateRequest request) {
		CompanyCreateResponse response = companyService.createCompany(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<ApiResponse<Void>> deleteCompany(
		@PathVariable UUID companyId) {
		companyService.deleteCompany(companyId);
		return ResponseEntity.ok().body(ApiResponse.ok(null));
	}

	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<CompanyUpdateResponse>> updateByCompany(
		@RequestBody CompanyUpdateByCompanyRequest request) {
		CompanyUpdateResponse response = companyService.updateByCompany(request);
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@PatchMapping("/{companyId}")
	public ResponseEntity<ApiResponse<CompanyUpdateResponse>> updateByAdmin(
		@RequestBody CompanyUpdateByAdminRequest request, @PathVariable UUID companyId) {
		CompanyUpdateResponse response = companyService.updateByAdmin(companyId, request);
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<CompanyInfoForCompanyResponse>> getInfoForCompany() {
		CompanyInfoForCompanyResponse response = companyService.getCompanyInfoForCompany();
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@GetMapping("/{companyId}")
	public ResponseEntity<ApiResponse<CompanyInfoForAdminResponse>> getInfoForAdmin(@PathVariable UUID companyId) {
		CompanyInfoForAdminResponse response = companyService.getCompanyInfoForAdmin(companyId);
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Slice<CompanyListResponse>>> getCompanies(
		@PageableDefault(size = 10, page = 0, sort = {"createdAt",
			"deletedAt"}) Pageable pageable) {
		int size = pageable.getPageSize();
		if (size != 10 && size != 30 && size != 50) {
			pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		}

		Slice<CompanyListResponse> response = companyService.getCompanies(pageable);
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}
}
