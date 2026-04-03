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

	public void updateInfo(String name, BigDecimal price, ProductStatus status) {
		if (name != null && !name.isBlank())
			this.name = name;
		this.price = validatePrice(price);
		updateStatus(status);
	}

	private BigDecimal validatePrice(BigDecimal price) {
		if (Objects.requireNonNull(price, "가격은 필수입니다.").compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
		}
		return price;
	}

	public void updateStatus(ProductStatus status) {
		this.status = status;
		if (status.equals(ProductStatus.STOPPED) || status.equals(ProductStatus.DISCONTINUED)
			|| status.equals(ProductStatus.OUT_OF_STOCK))
			this.isHide = true;
	}

	public void updateStock(Integer stock) {
		this.stockInfo.setStock(stock);
		if (stock == 0)
			updateStatus(ProductStatus.OUT_OF_STOCK);
		this.stockInfo.setStock(stock);
	}

	public void decreaseStock(Integer quantity) {
		this.stockInfo.decrease(quantity);
		if (this.stockInfo.getStock() == 0)
			updateStatus(ProductStatus.OUT_OF_STOCK);
	}

	public void restoreStock(Integer quantity) {
		this.stockInfo.restore(quantity);
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
