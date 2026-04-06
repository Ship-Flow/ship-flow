package com.shipflow.hubservice.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public final class HubRouteRequest {

	private HubRouteRequest() {
	}

	@Getter
	@NoArgsConstructor
	public static class Create {

		@NotNull
		private UUID departureHubId;

		@NotNull
		private UUID arrivalHubId;

		@NotNull
		private Integer duration;

		@NotNull
		private BigDecimal distance;
	}

	@Getter
	@NoArgsConstructor
	public static class Update {

		private Integer duration;

		private BigDecimal distance;
	}
}
