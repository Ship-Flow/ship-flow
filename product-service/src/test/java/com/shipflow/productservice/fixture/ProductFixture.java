package com.shipflow.productservice.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.model.ProductStatus;
import com.shipflow.productservice.infrastructure.persistence.ProductJpaEntity;

public class ProductFixture {
	public static Product create() {
		ProductJpaEntity entity = new ProductJpaEntity();
		ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(entity, "name", "testName");
		ReflectionTestUtils.setField(entity, "price", BigDecimal.valueOf(1000000));
		ReflectionTestUtils.setField(entity, "stock", 100);
		ReflectionTestUtils.setField(entity, "status", ProductStatus.OUT_OF_STOCK);
		ReflectionTestUtils.setField(entity, "companyId", UUID.randomUUID());
		ReflectionTestUtils.setField(entity, "companyName", "testCompanyName");
		ReflectionTestUtils.setField(entity, "hubId", UUID.randomUUID());
		ReflectionTestUtils.setField(entity, "isHide", false);
		ReflectionTestUtils.setField(entity, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(entity, "createdBy", UUID.randomUUID());
		return entity.toDomain();
	}
}
