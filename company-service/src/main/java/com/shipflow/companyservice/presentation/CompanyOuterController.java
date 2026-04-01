package com.shipflow.companyservice.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.companyservice.application.service.CompanyService;
import com.shipflow.companyservice.presentation.dto.request.CompanyCreateRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByAdminRequest;
import com.shipflow.companyservice.presentation.dto.request.CompanyUpdateByCompanyRequest;
import com.shipflow.companyservice.presentation.dto.response.CompanyCreateResponse;
import com.shipflow.companyservice.presentation.dto.response.CompanyUpdateResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController("/api/companies")
@RequiredArgsConstructor
public class CompanyOuterController {
	private final CompanyService companyService;

	@PostMapping("/")   //todo: 인증부분 확인 후 수정, 반환 데이터 추가 - dto
	public ResponseEntity<CompanyCreateResponse> createCompany(CompanyCreateRequest request,
		HttpServletRequest httpRequest) {
		UUID createrId = setUserInfo(httpRequest);
		CompanyCreateResponse response = companyService.createCompany(request, createrId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<String> deleteCompany(HttpServletRequest httpRequest, @PathVariable UUID companyId) {
		UUID deleterId = setUserInfo(httpRequest);
		companyService.deleteCompany(companyId, deleterId);
		return ResponseEntity.ok().body("요청이 정상 처리되었습니다.");
	}

	@PatchMapping("/me")
	public ResponseEntity<CompanyUpdateResponse> updateByCompany(HttpServletRequest httpRequest,
		CompanyUpdateByCompanyRequest request) {
		UUID updaterId = setUserInfo(httpRequest);
		CompanyUpdateResponse response = companyService.updateByCompany(request, updaterId);
		return ResponseEntity.ok().body(response);
	}

	@PatchMapping("/{companyId}")
	public ResponseEntity<CompanyUpdateResponse> updateByAdmin(HttpServletRequest httpRequest,
		CompanyUpdateByAdminRequest request, @PathVariable UUID companyId) {
		UUID updaterId = setUserInfo(httpRequest);
		CompanyUpdateResponse response = companyService.updateByAdmin(companyId, request, updaterId);
		return ResponseEntity.ok().body(response);
	}

	private UUID setUserInfo(HttpServletRequest request) {
		UserContext.setUserContext(request);
		return UserContext.getUserId();
	}
}
