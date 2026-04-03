package com.shipflow.productservice.infrastructure.web;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String userId = request.getHeader("X-User-Id");
		String userRole = request.getHeader("X-User-Role");

		if (userId != null && !userId.isBlank()) {
			try {
				UserContext.setUserId(UUID.fromString(userId));
				UserContext.setUserRole(userRole);
			} catch (IllegalArgumentException e) {
				log.error("Invalid UUID format in X-User-Id header: {}", userId);
			}
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		UserContext.clear();
	}
}
