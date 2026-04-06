package com.shipflow.productservice.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.productservice.application.client.VendorFeignClient;
import com.shipflow.productservice.application.dto.response.VendorInfoResponse;
import com.shipflow.productservice.application.mapper.ProductMapper;
import com.shipflow.productservice.domain.exception.ProductErrorCode;
import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.model.ProductStatus;
import com.shipflow.productservice.domain.repository.ProductRepository;
import com.shipflow.productservice.fixture.ProductFixture;
import com.shipflow.productservice.infrastructure.web.UserContext;
import com.shipflow.productservice.presentation.dto.request.ProductCreateRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateInfoRequest;
import com.shipflow.productservice.presentation.dto.request.ProductUpdateStockRequest;
import com.shipflow.productservice.presentation.dto.response.ProductInfoResponse;
import com.shipflow.productservice.presentation.dto.response.ProductListResponse;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;
	@Spy
	private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);
	@Mock
	private VendorFeignClient vendorClient;
	@Mock
	private RedisTemplate<String, Integer> redisTemplate;
	@Mock
	private ValueOperations<String, Integer> valueOperations;
	@InjectMocks
	ProductService productService;

	@Captor
	private ArgumentCaptor<Product> productCaptor;

	@AfterEach
	void tearDown() {
		UserContext.clear();
	}

	@Test
	void create() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		Product product = ProductFixture.create();
		ProductCreateRequest request = new ProductCreateRequest(product.getName(), product.getPrice(),
			product.getStock(), product.getStatus());
		VendorInfoResponse vendorInfo=new VendorInfoResponse(product.getCompanyId(), product.getCompanyName(), product.getHubId());
		given(vendorClient.getVendorInfo(product.getCompanyId())).willReturn(vendorInfo);
		given(productRepository.save(any(Product.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		doNothing().when(valueOperations).set(anyString(), any());

		//when
		productService.create(product.getCompanyId(), request);

		//then
		verify(productRepository).save(productCaptor.capture());
		Product savedProduct=productCaptor.getValue();
		assertThat(savedProduct.getName()).isEqualTo(product.getName());
		assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
		assertThat(savedProduct.getStock()).isEqualTo(product.getStock());
		assertThat(savedProduct.getStatus()).isEqualTo(product.getStatus());
		assertThat(savedProduct.getCompanyId()).isEqualTo(product.getCompanyId());
		assertThat(savedProduct.getCompanyName()).isEqualTo(product.getCompanyName());
		assertThat(savedProduct.getHubId()).isEqualTo(product.getHubId());
		;
	}

	@Test
	void delete() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		UUID productId = UUID.randomUUID();
		Product product = ProductFixture.create();
		UUID companyId = product.getCompanyId();
		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//when
		productService.delete(productId, companyId);

		//then
		verify(productRepository).save(productCaptor.capture());
		assertThat(productCaptor.getValue().getDeletedAt()).isNotNull();
	}

	@Test
	void updateInfo() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		Product product=ProductFixture.create();
		UUID companyId = product.getCompanyId();
		ProductUpdateInfoRequest request = new ProductUpdateInfoRequest(product.getName(), product.getPrice(),
			ProductStatus.ON_SALE);
		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

		//when
		productService.updateProductInfo(product.getId(), request, companyId);

		//then
		verify(productRepository).save(productCaptor.capture());
		assertThat(productCaptor.getValue().getName()).isEqualTo(request.name());
		assertThat(productCaptor.getValue().getPrice()).isEqualTo(request.price());
	}

	@Test
	void updateStock_성공() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		UUID productId = UUID.randomUUID();
		Product product = ProductFixture.create();
		UUID companyId = product.getCompanyId();
		ProductUpdateStockRequest request = new ProductUpdateStockRequest(100);
		given(productRepository.findById(productId)).willReturn(Optional.of(product));
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		//when
		productService.updateStock(productId, request, companyId);

		//then
		verify(productRepository).save(productCaptor.capture());
		assertThat(productCaptor.getValue().getStockInfo().getStock()).isEqualTo(1);
	}

	@Test
	void updateStock_실패_잘못된_재고값_입력() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		UUID productId = UUID.randomUUID();
		Product product = ProductFixture.create();
		UUID companyId = product.getCompanyId();
		ProductUpdateStockRequest request = new ProductUpdateStockRequest(-1);
		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//when&then
		assertThatThrownBy(() -> productService.updateStock(productId, request, companyId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("잘못된 재고값입니다.");

	}

	@Test
	void updateStock_재고를_0으로_설정(){
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		UUID productId = UUID.randomUUID();
		Product product = ProductFixture.create();
		UUID companyId = product.getCompanyId();
		ProductUpdateStockRequest request = new ProductUpdateStockRequest(0);
		given(productRepository.findById(productId)).willReturn(Optional.of(product));
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		//when
		productService.updateStock(productId, request, companyId);

		//then
		verify(productRepository).save(productCaptor.capture());
		assertThat(product.getStatus()).isEqualTo(ProductStatus.OUT_OF_STOCK);
	}

	@Test
	void getProductInfo_success() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		Product product = ProductFixture.create();
		UUID companyId = product.getCompanyId();
		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

		//when
		ProductInfoResponse response = productService.getProductInfo(product.getId(), companyId);

		//then
		assertThat(response.id()).isEqualTo(product.getId());
		assertThat(response.name()).isEqualTo(product.getName());
		assertThat(response.price()).isEqualTo(product.getPrice());
		assertThat(response.status()).isEqualTo(product.getStatus());
	}

	@Test
	void getProductList() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		UUID companyId = UUID.randomUUID();
		Product product = ProductFixture.create();
		List<Product> products = List.of(product);
		Pageable pageable = Pageable.ofSize(10);
		Slice<Product> slice = new SliceImpl<>(products, pageable, false);
		given(productRepository.findAllByCompanyId(companyId, pageable)).willReturn(slice);

		//when
		Slice<ProductListResponse> response=productService.getProductList(companyId, pageable);

		//then
		assertThat(response.getContent().size()).isEqualTo(products.size());
		assertThat(response.hasNext()).isFalse();

	}

	@Test
	void getStockInfoAndOccupy() {

	}

	@Test
	void decreaseStock_success() {
		//given
		Product product = ProductFixture.create();
		given(productRepository.findById(any())).willReturn(Optional.of(product));

		//when
		productService.decreaseStock(product.getId().toString(), 1);

		//then
		assertThat(product.getStockInfo().getStock())
			.isEqualTo(99);
	}

	@Test
	void decreaseStock_올바르지_않은_차감요청() {
		//given
		Product product = ProductFixture.create();

		//when&then
		assertThatThrownBy(() -> productService.decreaseStock(product.getId().toString(), -1))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ProductErrorCode.INVALID_ORDER_QUANTITY.message());
	}

	@Test
	void decreaseStock_재고보다_많은_차감요청() {
		//given
		Product product = ProductFixture.create();
		given(productRepository.findById(any())).willReturn(Optional.of(product));

		//when&then
		assertThatThrownBy(() -> productService.decreaseStock(product.getId().toString(), 101))
			.isInstanceOf(BusinessException.class)
			.hasMessage(ProductErrorCode.EXCEEDS_STOCK_LEVEL.message());
	}

	@Test
	void restoreStock() {
		//given
		Product product = ProductFixture.create();
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		//when
		productService.restoreStock(product.getId().toString(), 1);

		//then
		assertThat(product.getStockInfo().getStock())
			.isEqualTo(101);
	}

	@Test
	void deleteByCompany() {
		//given
		setHttpHeaders(UUID.randomUUID().toString());
		Product product = ProductFixture.create();
		List<Product> products = List.of(product);
		given(productRepository.findById(any(UUID.class))).willReturn(Optional.of(product));
		given(productRepository.findAllByCompanyId(any(UUID.class))).willReturn(products);

		//when
		productService.deleteByCompany(product.getCompanyId());

		//then
		assertThat(product.getDeletedBy()).isNotNull();
	}

	//util
	private void setHttpHeaders(String userId) {
		UserContext.setUserId(UUID.fromString(userId));
		UserContext.setUserRole("MASTER");
	}
}