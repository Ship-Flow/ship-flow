package com.shipflow.hubservice.domain.exception;

import com.shipflow.common.exception.ErrorCode;
import com.shipflow.common.exception.BusinessException;

public class HubException extends BusinessException {

	public HubException(ErrorCode errorCode) {
		super(errorCode);
	}

	public HubException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public HubException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	public HubException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
