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
import org.springframework.web.bind.annotation.RestController;

import com.shipflow.productservice.application.service.ProductService;
import com.shipflow.productservice.presentation.dto.request.ProductCreateRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateInfoRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateStockRequest;
import com.shipflow.productservice.presentation.dto.response.ProductCreateResponse;
import com.shipflow.productservice.presentation.dto.response.ProductInfoResponse;
import com.shipflow.productservice.presentation.dto.response.ProductListResponse;
import com.shipflow.productservice.presentation.dto.response.ProductUpdateResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController("/api/companies/{companyId}/products")
@RequiredArgsConstructor
public class ProductExternalController {
	private final ProductService productService;

	@PostMapping("/")
	public ResponseEntity<ProductCreateResponse> addProduct(@PathVariable UUID companyId,
		@RequestBody ProductCreateRequest productCreateRequest, HttpServletRequest request) {
		UUID createrId = getUserId(request);
		ProductCreateResponse response = productService.create(companyId, productCreateRequest, createrId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable UUID productId,
		HttpServletRequest request) {
		UUID deleterId = getUserId(request);
		productService.delete(deleterId, productId);
		return ResponseEntity.status(HttpStatus.OK).body("요청이 정상 처리되었습니다.");
	}

	@PatchMapping("/{productId}")
	public ResponseEntity<ProductUpdateResponse> updateProductInfo(@PathVariable UUID productId,
		@RequestBody ProductUpdateInfoRequest productUpdateInfoRequest,
		HttpServletRequest request) {
		UUID updaterId = getUserId(request);
		ProductUpdateResponse response = productService.updateInfo(productId, productUpdateInfoRequest, updaterId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/{productId}/stock")
	public ResponseEntity<ProductUpdateResponse> updateStock(@PathVariable UUID productId,
		ProductUpdateStockRequest productUpdateStockRequest, HttpServletRequest request) {
		UUID updaterId = getUserId(request);
		ProductUpdateResponse response = productService.updateStock(productId,
			productUpdateStockRequest, updaterId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductInfoResponse> getProductInfo(@PathVariable UUID productId) {
		ProductInfoResponse response = productService.getProductInfo(productId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping
	public ResponseEntity<Slice<ProductListResponse>> getProductList(@PathVariable UUID companyId,
		@PageableDefault(size = 10, page = 0, sort = {"createdAt",
			"deletedAt"}) Pageable pageable) {
		Slice<ProductListResponse> response = productService.getProductList(companyId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	//util
	private UUID getUserId(HttpServletRequest request) {
		UserContext.setUserContext(request);
		return UserContext.getUserId();
	}
}
