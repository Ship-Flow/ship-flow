package com.shipflow.companyservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserContext {
	private static final ThreadLocal<UUID> USER_ID_HOLDER = new ThreadLocal<>();
	private static final ThreadLocal<String> USER_ROLE_HOLDER = new ThreadLocal<>();

	public static void setUserContext(HttpServletRequest request) {
		String userId = request.getHeader("X-User-Id");
		String userRole = request.getHeader("X-User-Role");

		if (userId != null)
			USER_ID_HOLDER.set(UUID.fromString(userId));
		if (userRole != null)
			USER_ROLE_HOLDER.set(userRole);
	}

	public static UUID getUserId() {
		return USER_ID_HOLDER.get();
	}

	public static String getUserRole() {
		return USER_ROLE_HOLDER.get();
	}

	public static void clear() {
		USER_ID_HOLDER.remove();
		USER_ROLE_HOLDER.remove();
	}
}
