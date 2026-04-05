package com.shipflow.companyservice.infrastructure.context;

import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignUserContextInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		String userId = UserContext.getUserId().toString();
		String userRole = UserContext.getUserRole();

		if (userId != null) {
			template.header("X-User-Id", userId);
		}
		if (userRole != null) {
			template.header("X-User-Role", userRole);
		}
	}
}
