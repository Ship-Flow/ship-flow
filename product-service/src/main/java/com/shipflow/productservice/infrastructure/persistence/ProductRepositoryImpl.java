package com.shipflow.productservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
		ProductJpaEntity entity=jpaRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("해당 제품을 찾을 수 없습니다."));
		Product product=entity.toDomain();
		return Optional.of(product);
	}

	@Override
	public void save(Product product) {
		ProductJpaEntity entity=ProductJpaEntity.from(product);
		jpaRepository.save(entity);
	}

	@Override
	public List<Product> findAll() {
		List<ProductJpaEntity> entities=jpaRepository.findAll();
		return entities.stream()
			.map(ProductJpaEntity::toDomain)
			.toList();
	}
}
