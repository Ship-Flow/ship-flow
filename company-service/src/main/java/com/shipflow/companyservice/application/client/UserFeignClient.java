package com.shipflow.companyservice.application.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.shipflow.companyservice.application.dto.response.UserInfoResponse;

@FeignClient(name = "userservice")
public interface UserFeignClient {
	@GetMapping("/internal/users/{userId}")
	UserInfoResponse getUserInfoById(@PathVariable("userId") UUID userId);

	@PatchMapping("/internal/users/{userId}")
	UserInfoResponse updateCompanyManager(@PathVariable("userId") UUID userId, @RequestBody java.util.Map<String, Object> body);
}
