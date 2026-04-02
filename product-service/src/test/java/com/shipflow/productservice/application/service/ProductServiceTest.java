package com.shipflow.productservice.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockHttpServletRequest;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.productservice.application.client.VendorFeignClient;
import com.shipflow.productservice.application.dto.response.VendorInfoResponse;
import com.shipflow.productservice.application.mapper.ProductMapper;
import com.shipflow.productservice.domain.exception.ProductErrorCode;
import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.model.ProductStatus;
import com.shipflow.productservice.domain.repository.ProductRepository;
import com.shipflow.productservice.domain.vo.VendorInfo;
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
	ProductMapper mapper= Mappers.getMapper(ProductMapper.class);
	@Mock
	VendorFeignClient vendorClient;
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
		setHttpHeaders(UUID.randomUUID().toString(), "Company_Manager");
		Product product=ProductFixture.create();
		ProductCreateRequest request=new ProductCreateRequest(product.getName(), product.getPrice(),
			product.getStock(), product.getStatus());
		VendorInfoResponse vendorInfo=new VendorInfoResponse(product.getCompanyId(), product.getCompanyName(), product.getHubId());
		given(vendorClient.getVendorInfo(product.getCompanyId())).willReturn(vendorInfo);

		//when
		productService.create(product.getCompanyId(), request);

		//then
		verify(productRepository).save(productCaptor.capture());
		Product savedProduct=productCaptor.getValue();
		assertThat(savedProduct.getName()).isEqualTo(product.getName());
		assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
		assertThat(savedProduct.getStockInfo().getStock()).isEqualTo(product.getStock());
		assertThat(savedProduct.getStatus()).isEqualTo(product.getStatus());
		assertThat(savedProduct.getVendorInfo().getCompanyId()).isEqualTo(product.getCompanyId());
		assertThat(savedProduct.getVendorInfo().getCompanyName()).isEqualTo(product.getCompanyName());
		assertThat(savedProduct.getVendorInfo().getHubId()).isEqualTo(product.getHubId());
	}

	@Test
	void delete() {
		//given
		UUID productId=UUID.randomUUID();
		Product product=ProductFixture.create();
		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//when
		productService.delete(productId);

		//then
		verify(productRepository).save(productCaptor.capture());
		Product savedProduct=productCaptor.getValue();
		assertThat(savedProduct.getDeletedAt()).isNotNull();
	}

	@Test
	void updateInfo() {
		//given
		setHttpHeaders(UUID.randomUUID().toString(), "Company_Manager");
		Product product=ProductFixture.create();
		ProductUpdateInfoRequest request=new ProductUpdateInfoRequest(product.getName(), product.getPrice());
		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

		//when
		productService.updateInfo(product.getId(), request);

		//then
		verify(productRepository).save(productCaptor.capture());
		Product savedProduct=productCaptor.getValue();
		assertThat(savedProduct.getName()).isEqualTo(request.name());
		assertThat(savedProduct.getPrice()).isEqualTo(request.price());
	}

	@Test
	void updateStock_성공() {
		//given
		UUID productId=UUID.randomUUID();
		Product product=ProductFixture.create();
		ProductUpdateStockRequest request=new ProductUpdateStockRequest(100);
		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//when
		productService.updateStock(productId, request);

		//then
		verify(productRepository).save(productCaptor.capture());
		Product savedProduct=productCaptor.getValue();
		assertThat(savedProduct.getStockInfo().getStock()).isEqualTo(100);
	}

	@Test
	void updateStock_실패_잘못된_재고값_입력() {
		//given
		UUID productId=UUID.randomUUID();
		Product product=ProductFixture.create();
		ProductUpdateStockRequest request=new ProductUpdateStockRequest(-1);
		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//when&then
		assertThatThrownBy(() -> productService.updateStock(productId, request))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("잘못된 재고값입니다.");

	}

	@Test
	void updateStock_재고를_0으로_설정(){
		//given
		UUID productId=UUID.randomUUID();
		Product product=ProductFixture.create();
		ProductUpdateStockRequest request=new ProductUpdateStockRequest(0);
		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//when
		productService.updateStock(productId, request);

		//then
		verify(productRepository).save(productCaptor.capture());
		Product savedProduct=productCaptor.getValue();
		assertThat(savedProduct.getStatus()).isEqualTo(ProductStatus.OUT_OF_STOCK);
	}

	@Test
	void getProductInfo_success() {
		//given
		Product product=ProductFixture.create();
		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

		//when
		ProductInfoResponse response= productService.getProductInfo(product.getId());

		//then
		assertThat(response.id()).isEqualTo(product.getId());
		assertThat(response.name()).isEqualTo(product.getName());
		assertThat(response.price()).isEqualTo(product.getPrice());
		assertThat(response.status()).isEqualTo(product.getStatus());
	}

	@Test
	void getProductList() {
		//given
		UUID companyId=UUID.randomUUID();
		Product product=ProductFixture.create();
		List<Product> products=List.of(product);
		Pageable pageable=Pageable.ofSize(10);
		Slice<Product>slice=new SliceImpl<>(products, pageable, false);
		given(productRepository.findAllByCompanyId(companyId,pageable)).willReturn(slice);

		//when
		Slice<ProductListResponse> response=productService.getProductList(companyId, pageable);

		//then
		assertThat(response.getContent().size()).isEqualTo(products.size());
		assertThat(response.hasNext()).isFalse();

	}


	private void setHttpHeaders(String userId, String role) {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.addHeader("X-User-Id", userId);
		httpRequest.addHeader("X-User-Role", role);
		UserContext.setUserContext(httpRequest);
	}
}