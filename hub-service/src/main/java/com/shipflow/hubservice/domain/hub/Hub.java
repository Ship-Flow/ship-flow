package com.shipflow.hubservice.domain.hub;

import java.math.BigDecimal;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.hubservice.presentation.dto.HubRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_hubs")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hub extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@Column(nullable = false, length = 255)
	private String address;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(nullable = false)
	private UUID managerId;

	@Column(length = 20)
	private String managerName;

	public void update(HubRequest.Update request) {
		if (request.getName() != null) this.name = request.getName();
		if (request.getAddress() != null) this.address = request.getAddress();
		if (request.getLatitude() != null) this.latitude = request.getLatitude();
		if (request.getLongitude() != null) this.longitude = request.getLongitude();
		this.managerId = request.getManagerId();
		this.managerName = request.getManagerName();
	}

	public void delete(UUID userId) {
		softDelete(userId);
	}
}
