package com.shipflow.productservice.domain.vo;

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
			throw new IllegalArgumentException("재고는 0보다 작을 수 없습니다.");
		this.stock = stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public void decrease(Integer quantity) {
		if (quantity == null || quantity < 0)
			throw new IllegalArgumentException("유효하지 않은 수량입니다.");
		else if (quantity > stock)
			throw new IllegalArgumentException("보유 재고보다 많은 요청입니다.");
		this.stock -= quantity;
	}
}
