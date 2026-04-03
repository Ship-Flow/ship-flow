package com.shipflow.hubservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "companyservice")
public interface CompanyClient {

	@DeleteMapping("/internal/companies/{hubId}")
	void deleteCompaniesByHub(
		@RequestHeader("X-Internal-Request") String internalRequest,
		@RequestHeader("X-User-Id") String userId,
		@PathVariable("hubId") UUID hubId
	);
}
