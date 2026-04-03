package com.shipflow.productservice.infrastructure.config;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisInventoryLoader {

	private final ProductRepository productRepository;
	private final RedisTemplate<String, Integer> redisTemplate;

	public void loadInventoryToRedis() {
		List<Product> products = productRepository.findAll();

		products.forEach(product -> {
			String key="product:stock:"+product.getId();
			redisTemplate.opsForValue().set(key, product.getStock());
		});
	}
}
