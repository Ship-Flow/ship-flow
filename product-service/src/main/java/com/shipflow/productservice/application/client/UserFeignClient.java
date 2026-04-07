package com.shipflow.productservice.application.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shipflow.common.exception.ApiResponse;
import com.shipflow.productservice.application.dto.response.UserInfoResponse;

@FeignClient(name = "user-service")
public interface UserFeignClient {
	@GetMapping("/internal/users/{userId}")
	ApiResponse<UserInfoResponse> getUserInfoById(@PathVariable("userId") UUID userId);
}
