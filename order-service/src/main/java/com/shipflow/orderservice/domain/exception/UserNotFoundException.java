package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(OrderErrorCode.USER_NOT_FOUND);
    }
}
