package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(OrderErrorCode.UNAUTHORIZED);
    }
}
