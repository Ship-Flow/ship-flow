package com.shipflow.productservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.shipflow.productservice.domain.model.Product;

public interface ProductRepository {
	Optional<Product> findById(UUID id);

	Product save(Product product);

	Slice<Product> findAllByCompanyId(UUID companyId, Pageable pageable);

	List<Product> findAll();

	Integer findStockById(UUID productId);

	List<Product> findAllByCompanyId(UUID companyId);
}
