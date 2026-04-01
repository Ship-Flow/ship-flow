package com.shipflow.productservice.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Product extends BaseEntity {
	private UUID id;
	private String name;
	private BigDecimal price;
	private Integer stock;
	private ProductStatus status;
	private UUID companyId;
	private String companyName;
	private UUID hubId;
	private Boolean isHide;

	public static Product create(UUID id, String name, BigDecimal price, Integer stock, ProductStatus status,
		UUID companyId, String companyName, UUID hubId, Boolean isHide, UUID createdBy) {
		Product product = new Product(id, name, price, stock, status, companyId, companyName, hubId, isHide);
		product.create(createdBy);
		return product;
	}

	private Product(UUID id, String name, BigDecimal price, Integer stock, ProductStatus status,
		UUID companyId, String companyName, UUID hubId, Boolean isHide){
		this.id = id;
			this.name = Objects.requireNonNull(name, "name은 필수값입니다.");
			this.price = Objects.requireNonNull(price, "price는 필수값입니다.");
			this.stock = Objects.requireNonNullElse(stock, 0);
			this.status = Objects.requireNonNullElse(status, ProductStatus.STOPPED);
			this.companyId = Objects.requireNonNull(companyId, "companyId는 필수값입니다.");
			this.companyName = companyName;
			this.hubId = hubId;
			this.isHide = Objects.requireNonNullElse(isHide, false);
	}

	public static Product reconstruct(UUID id, String name, BigDecimal price, Integer stock, ProductStatus status,
		UUID companyId, String companyName, UUID hubId, Boolean isHide, UUID createdBy, LocalDateTime createdAt,
		LocalDateTime updatedAt, UUID updatedBy, LocalDateTime deletedAt, UUID deletedBy) {
		Product product = new Product(id, name, price, stock, status, companyId, companyName, hubId, isHide);
		product.createdAt = createdAt;
		product.createdBy = createdBy;
		product.updatedAt = updatedAt;
		product.updatedBy = updatedBy;
		product.deletedAt = deletedAt;
		product.deletedBy = deletedBy;
		return product;
	}

	public void updateInfo(String name, BigDecimal price, UUID updatedBy) {
		if (name != null && !name.isBlank())
			this.name = name;

		if(price.compareTo(BigDecimal.ZERO) <=0)
			throw new IllegalArgumentException("price는 0보다 커야 합니다.");
		else
			this.price = price;
		this.update(updatedBy);
	}

	public void delete(UUID deletedBy) {
		super.delete(deletedBy);
	}
}
