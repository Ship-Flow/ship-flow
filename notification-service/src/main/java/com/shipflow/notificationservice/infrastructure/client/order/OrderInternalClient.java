package com.shipflow.notificationservice.infrastructure.client.order;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderInternalClient {

	@GetMapping("/internal/orders/{orderId}/read-model")
	OrderReadModelResponse getOrderReadModel(@PathVariable UUID orderId);
}