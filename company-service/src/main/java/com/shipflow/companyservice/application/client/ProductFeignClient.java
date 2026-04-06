package com.shipflow.companyservice.application.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
	@DeleteMapping("/internal/companies/{companyId}/products/deactivate")
	Void deleteProductsByCompanyId(@PathVariable("companyId") UUID companyId);

	@DeleteMapping("/internal/companies/products/deactivate/bulk")
	Void deleteProductsByCompanyIdList(@RequestBody List<UUID> companyIds);
}
