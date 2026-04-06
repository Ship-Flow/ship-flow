package com.shipflow.productservice.infrastructure.messaging.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.shipflow.productservice.infrastructure.messaging.event.DeleteStockEvent;
import com.shipflow.productservice.infrastructure.messaging.event.UpdateStockEvent;

@Component
public class ProductCacheEventListener {

	private final RedisTemplate<String, Integer> redisTemplate;

	public ProductCacheEventListener(RedisTemplate<String, Integer> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleStockUpdate(UpdateStockEvent event) {
		redisTemplate.opsForValue().set("product:stock:" + event.productId(), event.stock());
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductDelete(DeleteStockEvent event) {
		redisTemplate.delete("product:stock:" + event.productId());
	}
}
