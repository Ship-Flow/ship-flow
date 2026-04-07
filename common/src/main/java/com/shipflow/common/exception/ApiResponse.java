package com.shipflow.common.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private final boolean success;
	private final T data;
	private final ErrorResponse error;

	@JsonCreator
	public static <T> ApiResponse<T> of(
		@JsonProperty("success") boolean success,
		@JsonProperty("data") T data,
		@JsonProperty("error") ErrorResponse error) {
		return new ApiResponse<>(success, data, error);
	}

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