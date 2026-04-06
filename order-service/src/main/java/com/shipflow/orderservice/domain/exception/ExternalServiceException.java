package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class ExternalServiceException extends BusinessException {

    public ExternalServiceException(Throwable cause) {
        super(OrderErrorCode.EXTERNAL_SERVICE_ERROR, cause);
    }
}
