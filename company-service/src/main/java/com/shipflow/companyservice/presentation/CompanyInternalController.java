package com.shipflow.companyservice.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.application.service.CompanyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController("/internal/companies")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyInternalController {
	private final CompanyService companyService;

	@GetMapping("/{companyId}")
	public ResponseEntity<VendorInfoResponse> getVendorById(@PathVariable("companyId") UUID companyId) {
		VendorInfoResponse response = companyService.getVendorInfo(companyId);
		return ResponseEntity.ok().body(response);
	}
}
