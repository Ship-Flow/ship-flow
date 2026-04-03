package com.shipflow.productservice.presentation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.productservice.application.dto.response.StockInfoResponse;
import com.shipflow.productservice.application.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class ProductInternalController {
	private final ProductService productService;

	@GetMapping("/{productId}")
	public ApiResponse<StockInfoResponse> getStockInfo(@PathVariable UUID productId,
		@RequestPart Integer quantity) {
		StockInfoResponse response = productService.getStockInfoAndOccupy(productId,quantity);
		return ApiResponse.ok(response);
	}
}
