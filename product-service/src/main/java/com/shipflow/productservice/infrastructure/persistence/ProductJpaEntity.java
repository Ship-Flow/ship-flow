package com.shipflow.productservice.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.productservice.domain.Product;
import com.shipflow.productservice.domain.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Table(name = "p_products")
public class ProductJpaEntity {
	@Id
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer stock;

	@Column(nullable = false)
	private ProductStatus status;

	@Column(columnDefinition = "uuid", nullable = false)
	private UUID companyId;

	private String companyName;

	@Column(columnDefinition = "uuid")
	private UUID hubId;

	private Boolean isHide;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(columnDefinition = "uuid")
	private UUID createdBy;

	private LocalDateTime updatedAt;

	@Column(columnDefinition = "uuid")
	private UUID updatedBy;

	private LocalDateTime deletedAt;

	@Column(columnDefinition = "uuid")
	private UUID deletedBy;

	public static ProductJpaEntity from(Product product) {
		ProductJpaEntity entity=new ProductJpaEntity();
		entity.id=product.getId();
		entity.name=product.getName();
		entity.price=product.getPrice();
		entity.stock=product.getStock();
		entity.status=product.getStatus();
		entity.companyId=product.getCompanyId();
		entity.companyName=product.getCompanyName();
		entity.hubId=product.getHubId();
		entity.isHide=product.getIsHide();
		entity.createdAt=product.getCreatedAt();
		entity.createdBy=product.getCreatedBy();
		entity.updatedAt=product.getUpdatedAt();
		entity.updatedBy=product.getUpdatedBy();
		entity.deletedAt=product.getDeletedAt();
		entity.deletedBy=product.getDeletedBy();
		return entity;
	}

	public Product toDomain() {
		return Product.reconstruct(id, name, price, stock,
			status, companyId, companyName, hubId, isHide,
			createdBy, createdAt, updatedAt, updatedBy, deletedAt, deletedBy);
	}
}
