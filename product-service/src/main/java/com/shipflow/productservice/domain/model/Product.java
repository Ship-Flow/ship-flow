package com.shipflow.productservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.shipflow.productservice.domain.vo.StockInfo;
import com.shipflow.productservice.domain.vo.VendorInfo;

import lombok.Getter;

@Getter
public class Product extends BaseEntity {
	private UUID id;
	private String name;
	private BigDecimal price;
	private ProductStatus status;
	private Boolean isHide;
	private StockInfo stockInfo;
	private VendorInfo vendorInfo;

	private Product(String name, BigDecimal price, Integer stock, ProductStatus status,
		UUID companyId, String companyName, UUID hubId) {
		this.name = Objects.requireNonNull(name, "name은 필수값입니다.");
		this.price = Objects.requireNonNull(price, "price는 필수값입니다.");
		this.status = Objects.requireNonNullElse(status, ProductStatus.STOPPED);
		this.isHide = this.status == ProductStatus.STOPPED || stock == 0;
		this.stockInfo = new StockInfo(stock);
		this.vendorInfo = new VendorInfo(companyId, companyName, hubId);
	}

	public static Product create(String name, BigDecimal price, Integer stock, ProductStatus status,
		UUID companyId, String companyName, UUID hubId, UUID createdBy) {
		Product product = new Product(name, price, stock, status, companyId, companyName, hubId);
		product.create(createdBy);
		return product;
	}

	public static Product reconstruct(UUID id,String name, BigDecimal price,
		Integer stock, ProductStatus status, UUID companyId, String companyName,
		UUID hubId, Boolean isHide, UUID createdBy, LocalDateTime createdAt,
		LocalDateTime updatedAt, UUID updatedBy, LocalDateTime deletedAt, UUID deletedBy) {
		Product product = new Product(name, price, stock, status, companyId, companyName, hubId);
		product.id=id;
		product.isHide = isHide;
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

		if (price.compareTo(BigDecimal.ZERO) <= 0)
			throw new IllegalArgumentException("price는 0보다 커야 합니다.");
		else
			this.price = price;
		this.update(updatedBy);
	}

	public void updateVendorInfo(UUID companyId, String companyName, UUID hubId, UUID updatedBy) {
		this.vendorInfo = new VendorInfo(companyId, companyName, hubId);
		this.update(updatedBy);
	}

	public void updateStatus(ProductStatus status, UUID updatedBy) {
		this.status = status;
		if (status.equals(ProductStatus.STOPPED) || status.equals(ProductStatus.DISCONTINUED)
			|| status.equals(ProductStatus.OUT_OF_STOCK))
			this.isHide = true;
		this.update(updatedBy);
	}

	public void updateStock(Integer stock, UUID updatedBy) {
		this.stockInfo.setStock(stock);
		if (stock == 0)
			updateStatus(ProductStatus.OUT_OF_STOCK, updatedBy);
		this.stockInfo.setStock(stock);
		this.update(updatedBy);
	}

	public void decreaseStock(Integer quantity) {
		this.stockInfo.decrease(quantity);
		if (this.stockInfo.getStock() == 0)
			this.isHide = true;
	}

	public void delete(UUID deletedBy) {
		super.delete(deletedBy);
		this.isHide = true;
	}

	public Integer getStock() {
		return this.stockInfo.getStock();
	}

	public UUID getCompanyId () {
		return this.vendorInfo.getCompanyId();
	}

	public String getCompanyName () {
		return this.vendorInfo.getCompanyName();
	}

	public UUID getHubId () {
		return this.vendorInfo.getHubId();
	}
}
