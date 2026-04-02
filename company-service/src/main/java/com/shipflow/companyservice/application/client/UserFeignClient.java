package com.shipflow.companyservice.application.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shipflow.companyservice.application.dto.response.UserInfoResponse;

@FeignClient(name = "user-service")
public interface UserFeignClient {
	@GetMapping("/internal/users/{userId}")
	UserInfoResponse getUserNameById(@PathVariable("userId") UUID userId);
}
