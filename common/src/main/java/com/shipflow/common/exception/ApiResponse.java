package com.shipflow.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private final boolean success;
	private final T data;
	private final ErrorResponse error;

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static ApiResponse<Void> fail(ErrorCode errorCode, String path) {
		return new ApiResponse<>(false, null,
			new ErrorResponse(errorCode.code(), errorCode.message(), path));
	}

	public static ApiResponse<Void> fail(ErrorCode errorCode, String message, String path) {
		return new ApiResponse<>(false, null,
			new ErrorResponse(errorCode.code(), message, path));
	}

	@Getter
	@AllArgsConstructor
	public static class ErrorResponse {
		private final String code;
		private final String message;
		private final String path;
	}
}