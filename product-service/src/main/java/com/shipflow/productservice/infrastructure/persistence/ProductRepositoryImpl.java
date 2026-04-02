package com.shipflow.productservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
	private final ProductJpaRepository jpaRepository;

	`@Override`
	public Optional<Product> findById(UUID id) {
		return jpaRepository.findById(id)
			.map(ProductJpaEntity::toDomain);
	}

	@Override
	public Product save(Product product) {
		ProductJpaEntity entity = ProductJpaEntity.from(product);
		ProductJpaEntity savedEntity = jpaRepository.save(entity);
		return savedEntity.toDomain();
	}

	@Override
	public Slice<Product> findAllByCompanyId(UUID companyId, Pageable pageable) {
		Slice<ProductJpaEntity> entities = jpaRepository.findAllByCompanyId(companyId, pageable);
		return entities.map(ProductJpaEntity::toDomain);
	}
}
