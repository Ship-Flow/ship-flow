package com.shipflow.hubservice.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

public final class HubResponse {

	private HubResponse() {
	}

	@Getter
	@Builder
	public static class Detail {

		private UUID id;
		private String name;
		private String address;
		private BigDecimal latitude;
		private BigDecimal longitude;
		private UUID managerId;
		private String managerName;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

	@Getter
	@Builder
	public static class Summary {

		private UUID id;
		private String name;
		private String address;
		private BigDecimal latitude;
		private BigDecimal longitude;
		private UUID managerId;
		private String managerName;
		private LocalDateTime createdAt;
	}
}
