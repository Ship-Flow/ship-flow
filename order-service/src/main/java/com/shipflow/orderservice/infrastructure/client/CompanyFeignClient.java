package com.shipflow.orderservice.infrastructure.client;

import com.shipflow.orderservice.infrastructure.client.dto.ReceiverCompanyInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "companyservice")
public interface CompanyFeignClient {

    @GetMapping("/internal/companies/{companyId}")
    ReceiverCompanyInfo getCompanyInfo(
            @RequestHeader("X-Internal-Request") String internalRequest,
            @PathVariable("companyId") UUID companyId
    );
}
