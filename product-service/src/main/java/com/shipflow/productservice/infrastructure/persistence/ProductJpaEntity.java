package com.shipflow.productservice.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.domain.model.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_product")
public class ProductJpaEntity extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer stock;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private ProductStatus status;

	@Column(columnDefinition = "uuid", nullable = false)
	private UUID companyId;

	private String companyName;

	@Column(columnDefinition = "uuid")
	private UUID hubId;

	private Boolean isHide;

	public static ProductJpaEntity from(Product product) {
		ProductJpaEntity entity = new ProductJpaEntity();
		entity.id = product.getId();
		entity.name = product.getName();
		entity.price = product.getPrice();
		entity.stock = product.getStockInfo().getStock();
		entity.status = product.getStatus();
		entity.companyId = product.getVendorInfo().getCompanyId();
		entity.companyName = product.getVendorInfo().getCompanyName();
		entity.hubId = product.getVendorInfo().getHubId();
		entity.isHide = product.getIsHide();
		entity.createdAt = product.getCreatedAt();
		entity.createdBy = product.getCreatedBy();
		entity.updatedAt = product.getUpdatedAt();
		entity.updatedBy = product.getUpdatedBy();
		entity.deletedAt = product.getDeletedAt();
		entity.deletedBy = product.getDeletedBy();
		return entity;
	}

	public Product toDomain() {
		return Product.reconstruct(id, name, price, stock,
			status, companyId, companyName, hubId, isHide,
			createdBy, createdAt, updatedAt, updatedBy, deletedAt, deletedBy);
	}
}
