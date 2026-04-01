package com.shipflow.productservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.productservice.application.client.VendorFeignClient;
import com.shipflow.productservice.application.dto.response.VendorInfoResponse;
import com.shipflow.productservice.application.mapper.ProductMapper;
import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.repository.ProductRepository;
import com.shipflow.productservice.presentation.dto.request.ProductCreateRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateInfoRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateStockRequest;
import com.shipflow.productservice.presentation.dto.response.ProductCreateResponse;
import com.shipflow.productservice.presentation.dto.response.ProductUpdateResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final ProductMapper mapper;
	private final VendorFeignClient vendorClient;

	@Transactional
	public ProductCreateResponse create(UUID companyId, ProductCreateRequest request, UUID createrId) {
		VendorInfoResponse response = vendorClient.getVendorInfo(companyId);
		Product product = Product.create(
			request.name(), request.price(), request.stock(),
			request.status(), companyId, response.companyName(), response.hubId(),
			createrId);
		productRepository.save(product);
		return mapper.toCreateResponse(product);
	}

	@Transactional
	public void delete(UUID productId, UUID deleterId) {
		Product product = findUserById(productId);
		product.delete(deleterId);
		productRepository.save(product);
	}

	@Transactional
	public ProductUpdateResponse updateInfo(UUID productId, ProductUpdateInfoRequest request, UUID updaterId) {
		Product product = findUserById(productId);
		product.updateInfo(
			request.productName(), request.price(), updaterId
		);
		productRepository.save(product);
		return mapper.toUpdateResponse(product);
	}

	@Transactional
	public ProductUpdateResponse updateStock(UUID productId, ProductUpdateStockRequest request, UUID updaterId) {
		Product product = findUserById(productId);
		product.updateStock(request.stock(), updaterId);
		return mapper.toUpdateResponse(product);
	}

	private Product findUserById(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));
	}
}
