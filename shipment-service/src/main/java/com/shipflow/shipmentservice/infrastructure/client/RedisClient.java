package com.shipflow.shipmentservice.infrastructure.client;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.shipflow.shipmentservice.application.client.CacheClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisClient implements CacheClient {

	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public long increment(String key) {
		Long value = redisTemplate.opsForValue().increment(key);
		return value != null ? value : 0L;
	}

	@Override
	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	@Override
	public void set(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}
}
