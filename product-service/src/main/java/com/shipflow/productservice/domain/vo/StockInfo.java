package com.shipflow.productservice.domain.vo;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.productservice.domain.exception.ProductErrorCode;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockInfo {
	private Integer stock;

	public StockInfo(Integer stock) {
		if (stock == null || stock < 0)
			throw new BusinessException(ProductErrorCode.INVALID_STOCK_VALUE);
		this.stock = stock;
	}

	public void setStock(Integer stock) {
		if (stock == null || stock < 0)
			throw new BusinessException(ProductErrorCode.INVALID_STOCK_VALUE);
		this.stock = stock;
	}

	public void decrease(Integer quantity) {
		if (quantity == null || quantity < 0)
			throw new BusinessException(ProductErrorCode.INVALID_ORDER_QUANTITY);
		else if (quantity > stock)
			throw new BusinessException(ProductErrorCode.OUT_OF_STOCK);
		this.stock -= quantity;
	}
}
