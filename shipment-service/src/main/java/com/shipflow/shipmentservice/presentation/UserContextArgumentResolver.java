package com.shipflow.shipmentservice.presentation;

import java.util.UUID;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;

import jakarta.servlet.http.HttpServletRequest;

public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String USER_ID_HEADER = "X-User-Id";
	private static final String USER_ROLE_HEADER = "X-User-Role";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(UserContext.class);
	}

	@Override
	public UserContext resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		String userId = request.getHeader(USER_ID_HEADER);
		String role = request.getHeader(USER_ROLE_HEADER);

		if (userId == null || userId.isBlank()) {
			throw new BusinessException(ShipmentErrorCode.MISSING_USER_ID);
		}
		if (role == null || role.isBlank()) {
			throw new BusinessException(ShipmentErrorCode.MISSING_USER_ROLE);
		}

		return new UserContext(UUID.fromString(userId), role);
	}
}