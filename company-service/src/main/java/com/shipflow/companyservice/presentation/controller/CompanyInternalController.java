package com.shipflow.companyservice.presentation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.companyservice.application.dto.response.VendorInfoResponse;
import com.shipflow.companyservice.application.service.CompanyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/companies")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyInternalController {
	private final CompanyService companyService;

	@GetMapping("/{companyId}")
	public VendorInfoResponse getVendorById(@PathVariable("companyId") UUID companyId) {
		return companyService.getVendorInfo(companyId);
	}

	@DeleteMapping("/{hubId}")
	public void deleteByHub(@PathVariable("hubId") UUID hubId) {
		companyService.deleteProductsByHub(hubId);
	}
}
