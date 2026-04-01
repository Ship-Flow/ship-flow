package com.shipflow.productservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.shipflow.productservice.domain.model.Product;

public interface ProductRepository {
	Optional<Product> findById(UUID id);
	void save(Product product);
	List<Product> findAll();
}
