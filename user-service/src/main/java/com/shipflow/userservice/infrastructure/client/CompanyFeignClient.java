package com.shipflow.userservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "companyservice")
public interface CompanyFeignClient {
	@DeleteMapping("/internal/companies/{userId}")
	ClientApiResponse<Void> deleteManager(@PathVariable UUID userId);
}
