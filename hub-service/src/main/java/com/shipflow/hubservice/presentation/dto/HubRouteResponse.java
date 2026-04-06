package com.shipflow.hubservice.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

public final class HubRouteResponse {

	private HubRouteResponse() {
	}

	@Getter
	@Builder
	public static class Detail {

		private UUID id;
		private UUID departureHubId;
		private String departureHubName;
		private UUID arrivalHubId;
		private String arrivalHubName;
		private Integer duration;
		private BigDecimal distance;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

	@Getter
	@Builder
	public static class Summary {

		private UUID id;
		private UUID departureHubId;
		private String departureHubName;
		private UUID arrivalHubId;
		private String arrivalHubName;
		private Integer duration;
		private BigDecimal distance;
		private LocalDateTime createdAt;
	}
}
