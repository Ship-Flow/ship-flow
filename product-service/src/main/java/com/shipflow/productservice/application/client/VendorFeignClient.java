package com.shipflow.productservice.application.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shipflow.productservice.application.dto.response.VendorInfoResponse;

@FeignClient(name = "companyservice")
public interface VendorFeignClient {
	@GetMapping("/internal/companies/{companyId}")
	VendorInfoResponse getVendorInfo(@PathVariable("companyId") UUID companyId);
}
