package com.shipflow.hubservice.domain.hub;

import java.math.BigDecimal;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.hubservice.presentation.dto.HubRouteRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_hub_routes")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubRoute extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "uuid")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "departure_hub_id", nullable = false)
	private Hub departureHub;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "arrival_hub_id", nullable = false)
	private Hub arrivalHub;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal distance;

	@Column(nullable = false)
	private Integer duration;

	public void update(HubRouteRequest.Update request) {
		if (request.getDuration() != null) this.duration = request.getDuration();
		if (request.getDistance() != null) this.distance = request.getDistance();
	}

	public void delete(UUID userId) {
		softDelete(userId);
	}
}
