package com.shipflow.userservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyFeignClient {
	@DeleteMapping("/internal/companies/{userId}")
	void deleteManager(@PathVariable UUID userId);
}
