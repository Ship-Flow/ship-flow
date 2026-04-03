package com.shipflow.productservice.infrastructure.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisInventoryLoader implements ApplicationRunner {

	private final ProductRepository productRepository;
	private final RedisTemplate<String, Integer> redisTemplate;

	@Override
	public void run(ApplicationArguments args) {
		loadInventoryToRedis();
	}

	public void loadInventoryToRedis() {
		List<Product> products = productRepository.findAll();

		redisTemplate.executePipelined((RedisCallback<Object>)connection -> {
			products.forEach(product -> {
				String key = "product:stock:" + product.getId();
				redisTemplate.opsForValue().set(key, product.getStock());
			});
			return null;
		});
	}
}
