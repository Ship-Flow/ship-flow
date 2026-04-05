package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class InvalidOrderStateException extends BusinessException {

    public InvalidOrderStateException(String message) {
        super(OrderErrorCode.INVALID_ORDER_STATE, message);
    }
}
