package com.shipflow.productservice.application.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shipflow.productservice.application.dto.response.VendorInfoResponse;

@FeignClient(name = "company-service"/*,url="${}"*/)//todo: yaml 파일 설정 추가 후 url설정
public interface VendorFeignClient {
	@GetMapping("/internal/companies/{companyId}")
	VendorInfoResponse getVendorInfo(@PathVariable("companyId") UUID companyId);
}
