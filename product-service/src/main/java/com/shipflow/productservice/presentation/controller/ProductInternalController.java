package com.shipflow.productservice.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.productservice.application.dto.response.StockInfoResponse;
import com.shipflow.productservice.application.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductInternalController {
	private final ProductService productService;

	@GetMapping("/internal/companies/{companyId}/products/{productId}")
	public ApiResponse<StockInfoResponse> getStockInfo(@PathVariable UUID productId,
		@RequestParam Integer quantity) {
		StockInfoResponse response = productService.getStockInfoAndOccupy(productId,quantity);
		return ApiResponse.ok(response);
	}

	@DeleteMapping("/internal/companies/{companyId}/products/deactivate")
	public ApiResponse<Void> deleteByCompany(@PathVariable UUID companyId) {
		productService.deleteByCompany(companyId);
		return ApiResponse.ok(null);
	}

	@DeleteMapping("/internal/companies/products/deactivate/bulk")
	public ApiResponse<Void> deleteByHub(@RequestBody List<UUID> companyIds) {
		productService.deleteByHub(companyIds);
		return ApiResponse.ok(null);
	}
}
