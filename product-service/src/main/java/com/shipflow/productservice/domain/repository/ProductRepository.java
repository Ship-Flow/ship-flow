package com.shipflow.productservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.shipflow.productservice.domain.model.Product;

public interface ProductRepository {
	Optional<Product> findById(UUID id);

	void save(Product product);

	Slice<Product> findAllByCompanyId(UUID companyId, Pageable pageable);
}
