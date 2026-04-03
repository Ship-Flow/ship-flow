package com.shipflow.productservice.presentation;

import java.util.UUID;

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
import com.shipflow.productservice.application.service.ProductService;
import com.shipflow.productservice.infrastructure.web.UserContext;
import com.shipflow.productservice.presentation.dto.request.ProductCreateRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateInfoRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateStockRequest;
import com.shipflow.productservice.presentation.dto.response.ProductCreateResponse;
import com.shipflow.productservice.presentation.dto.response.ProductInfoResponse;
import com.shipflow.productservice.presentation.dto.response.ProductListResponse;
import com.shipflow.productservice.presentation.dto.response.ProductUpdateResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies/{companyId}/products")
@RequiredArgsConstructor
public class ProductExternalController {
	private final ProductService productService;

	@PostMapping
	public ResponseEntity<ApiResponse<ProductCreateResponse>> addProduct(@PathVariable UUID companyId,
		@Valid @RequestBody ProductCreateRequest productCreateRequest, HttpServletRequest request) {
		UserContext.setUserContext(request);
		ProductCreateResponse response = productService.create(companyId, productCreateRequest);
		UserContext.clear();
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable UUID productId,
		HttpServletRequest request) {
		UserContext.setUserContext(request);
		productService.delete(productId);
		UserContext.clear();
		return ResponseEntity.status(HttpStatus.OK).body("요청이 정상 처리되었습니다.");
	}

	@PatchMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductUpdateResponse>> updateProductInfo(@PathVariable UUID productId,
		@RequestBody ProductUpdateInfoRequest productUpdateInfoRequest,
		HttpServletRequest request) {
		UserContext.setUserContext(request);
		ProductUpdateResponse response = productService.updateProductInfo(productId, productUpdateInfoRequest);
		UserContext.clear();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@PostMapping("/{productId}/stock")
	public ResponseEntity<ApiResponse<ProductUpdateResponse>> updateStock(@PathVariable UUID productId,
		@Valid @RequestBody ProductUpdateStockRequest productUpdateStockRequest, HttpServletRequest request) {
		UserContext.setUserContext(request);
		ProductUpdateResponse response = productService.updateStock(productId,
			productUpdateStockRequest);
		UserContext.clear();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductInfoResponse>> getProductInfo(@PathVariable UUID productId) {
		ProductInfoResponse response = productService.getProductInfo(productId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Slice<ProductListResponse>>> getProductList(@PathVariable UUID companyId,
		@PageableDefault(size = 10, page = 0, sort = {"createdAt",
			"deletedAt"}) Pageable pageable) {
		Slice<ProductListResponse> response = productService.getProductList(companyId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
	}
}
