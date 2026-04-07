package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException() {
        super(OrderErrorCode.PRODUCT_NOT_FOUND);
    }
}
