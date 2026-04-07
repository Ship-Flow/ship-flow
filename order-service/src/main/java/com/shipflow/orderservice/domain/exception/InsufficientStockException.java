package com.shipflow.orderservice.domain.exception;

import com.shipflow.common.exception.BusinessException;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException() {
        super(OrderErrorCode.PRODUCT_INSUFFICIENT_STOCK);
    }
}
