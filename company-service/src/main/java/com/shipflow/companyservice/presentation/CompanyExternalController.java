package com.shipflow.companyservice.presentation;

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
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.companyservice.application.service.CompanyService;
import com.shipflow.companyservice.infrastructure.web.UserContext;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByAdminRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByCompanyRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForAdminResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyInfoForCompanyResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyListResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController("/api/companies")
@RequiredArgsConstructor
public class CompanyExternalController {
	private final CompanyService companyService;

	@PostMapping("/")
	public ResponseEntity<ApiResponse<CompanyCreateResponse>> createCompany(CompanyCreateRequest request,
		HttpServletRequest httpRequest) {
		UserContext.setUserContext(httpRequest);
		CompanyCreateResponse response = companyService.createCompany(request);
		UserContext.clear();
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
	}

	@DeleteMapping("/{companyId}")    //todo: 서비스에서 사용자 권한 추가 검증 고려
	public ResponseEntity<ApiResponse<Void>> deleteCompany(HttpServletRequest httpRequest,
		@PathVariable UUID companyId) {
		UserContext.setUserContext(httpRequest);
		companyService.deleteCompany(companyId);
		return ResponseEntity.ok().body(ApiResponse.ok(null));
	}

	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<CompanyUpdateResponse>> updateByCompany(HttpServletRequest httpRequest,
		CompanyUpdateByCompanyRequest request) {
		UserContext.setUserContext(httpRequest);
		CompanyUpdateResponse response = companyService.updateByCompany(request);
		UserContext.clear();
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@PatchMapping("/{companyId}")
	public ResponseEntity<ApiResponse<CompanyUpdateResponse>> updateByAdmin(HttpServletRequest httpRequest,
		CompanyUpdateByAdminRequest request, @PathVariable UUID companyId) {
		UserContext.setUserContext(httpRequest);
		CompanyUpdateResponse response = companyService.updateByAdmin(companyId, request);
		UserContext.clear();
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<CompanyInfoForCompanyResponse>> getInfoForCompany(
		HttpServletRequest httpRequest) {
		UserContext.setUserContext(httpRequest);
		CompanyInfoForCompanyResponse response = companyService.getCompanyInfoForCompany();
		UserContext.clear();
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@GetMapping("/{companyId}")
	public ResponseEntity<ApiResponse<CompanyInfoForAdminResponse>> getInfoForAdmin(@PathVariable UUID companyId) {
		CompanyInfoForAdminResponse response = companyService.getCompanyInfoForAdmin(companyId);
		return ResponseEntity.ok().body(ApiResponse.ok(response));
	}

	@GetMapping("/")
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
