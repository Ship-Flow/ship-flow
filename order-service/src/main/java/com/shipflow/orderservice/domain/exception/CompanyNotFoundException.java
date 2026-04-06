package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class CompanyNotFoundException extends BusinessException {

    public CompanyNotFoundException() {
        super(OrderErrorCode.COMPANY_NOT_FOUND);
    }
}
