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

	@Override
	public Optional<Product> findById(UUID id) {
		ProductJpaEntity entity = jpaRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("해당 제품을 찾을 수 없습니다."));
		Product product = entity.toDomain();
		return Optional.of(product);
	}

	@Override
	public void save(Product product) {
		ProductJpaEntity entity = ProductJpaEntity.from(product);
		jpaRepository.save(entity);
	}

	@Override
	public Slice<Product> findAllByCompanyId(UUID companyId, Pageable pageable) {
		Slice<ProductJpaEntity> entities = jpaRepository.findAllByCompanyId(companyId, pageable);
		return entities.map(ProductJpaEntity::toDomain);
	}
}
