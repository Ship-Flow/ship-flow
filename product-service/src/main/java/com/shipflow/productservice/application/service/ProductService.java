package com.shipflow.productservice.application.service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.common.exception.CommonErrorCode;
import com.shipflow.productservice.application.client.VendorFeignClient;
import com.shipflow.productservice.application.dto.response.StockInfoResponse;
import com.shipflow.productservice.application.dto.response.VendorInfoResponse;
import com.shipflow.productservice.application.mapper.ProductMapper;
import com.shipflow.productservice.domain.exception.ProductErrorCode;
import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.repository.ProductRepository;
import com.shipflow.productservice.infrastructure.web.UserContext;
import com.shipflow.productservice.presentation.dto.request.ProductCreateRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateInfoRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateStockRequest;
import com.shipflow.productservice.presentation.dto.response.ProductCreateResponse;
import com.shipflow.productservice.presentation.dto.response.ProductInfoResponse;
import com.shipflow.productservice.presentation.dto.response.ProductListResponse;
import com.shipflow.productservice.presentation.dto.response.ProductUpdateResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final ProductMapper mapper;
	private final VendorFeignClient vendorClient;
	private final RedisTemplate<String, Integer> redisTemplate;

	//external
	@Transactional
	public ProductCreateResponse create(UUID companyId, ProductCreateRequest request) {
		UUID createrId = UserContext.getUserId();
		VendorInfoResponse response = vendorClient.getVendorInfo(companyId);
		Product product = Product.create(
			request.name(), request.price(), request.stock(),
			request.status(), companyId, response.name(), response.hubId(),
			createrId);
		Product savedProduct =productRepository.save(product);
		return mapper.toCreateResponse(savedProduct);
	}

	@Transactional
	public void delete(UUID productId) {
		UUID deleterId = UserContext.getUserId();
		Product product = findProductById(productId);
		product.delete(deleterId);
		productRepository.save(product);
	}

	@Transactional
	public ProductUpdateResponse updateProductInfo(UUID productId, ProductUpdateInfoRequest request) {
		Product product = findProductById(productId);
		product.updateInfo(
			request.name(), request.price(), request.status()
		);
		productRepository.save(product);
		return mapper.toUpdateResponse(product);
	}

	@Transactional
	public ProductUpdateResponse updateStock(UUID productId, ProductUpdateStockRequest request) {
		Product product = findProductById(productId);
		product.updateStock(request.stock());
		productRepository.save(product);
		return mapper.toUpdateResponse(product);
	}

	public ProductInfoResponse getProductInfo(UUID productId) {
		Product product = findProductById(productId);
		return mapper.toProductInfoResponse(product);
	}

	public Slice<ProductListResponse> getProductList(UUID companyId, Pageable pageable) {
		Slice<Product> products = productRepository.findAllByCompanyId(companyId, pageable);
		return products.map(mapper::toProductListResponse);
	}


	//internal

	/*
	* 주문 시 product 측 흐름 :
	* 1. 주문 전 재고 조회 요청 시) querystring으로 요청 재고 값 전달받음
	* 2. 재고 조회 시) redis에서 조회 후 없으면 db에서 조회 -> redis에 저장 -> 재고 조회 시 redis에서 조회 -> 없으면 db에서 조회 -> redis에 저장 / 없으면 예외 발생
	* 3. 조회 성공 시) 재고 감소 -> redis에 선점 정보 저장(ttl 5초) / 재고가 주문량보다 적을 경우 차감없이 재고 반환
	* 4. 선점 후 주문 이벤트 발생 시) redis 선점 정보 삭제 -> db에서 재고 차감
	* */

	// 재고 조회
	public StockInfoResponse getStockInfoAndOccupy(@Param("productId") UUID productId, Integer quantity) {
		String stockKey ="product:stock:"+productId;
		String occupancyKey ="product:"+ UserContext.getUserId() +":"+productId;

		ensureStockInRedis(productId, stockKey);

		Long currentStock = Optional.ofNullable(redisTemplate.opsForValue().decrement(stockKey, quantity))
			.orElseThrow(() -> new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR));

		if(currentStock<0){
			Long restoreStock=redisTemplate.opsForValue().increment(stockKey, (long)quantity);
			return new StockInfoResponse(productId, restoreStock!=null?restoreStock.intValue():currentStock.intValue());
		}

		redisTemplate.opsForValue().set(occupancyKey, quantity, Duration.ofSeconds(5));

		return new StockInfoResponse(productId, currentStock.intValue());
	}

	public void decreaseStock(String productId, Integer quantity) {
		Product product = findProductById(UUID.fromString(productId));

		if (product.getStock() < quantity) {
			throw new BusinessException(ProductErrorCode.EXCEEDS_STOCK_LEVEL);
		}

		product.decreaseStock(quantity);
		productRepository.save(product);
	}

	public void restoreStock(String productId, Integer quantity) {
		Product product = findProductById(UUID.fromString(productId));
		product.restoreStock(quantity);
		productRepository.save(product);
	}


	//util
	private Product findProductById(UUID productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
	}

	private void ensureStockInRedis(UUID productId, String stockKey) {
		if (!redisTemplate.hasKey(stockKey)) {

			Integer dbStock = productRepository.findStockById(productId);

			if (dbStock == null) {
				throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
			}

			redisTemplate.opsForValue().setIfAbsent(stockKey, dbStock);
		}
	}
}
