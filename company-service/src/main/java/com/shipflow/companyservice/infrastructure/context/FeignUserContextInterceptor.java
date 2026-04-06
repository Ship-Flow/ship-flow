package com.shipflow.companyservice.infrastructure.context;

import java.util.UUID;

import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignUserContextInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		UUID userId = UserContext.getUserId();
		String userRole = UserContext.getUserRole();

		if (userId != null) {
			template.header("X-User-Id", userId.toString());
		}
		if (userRole != null) {
			template.header("X-User-Role", userRole);
		}
	}
}
