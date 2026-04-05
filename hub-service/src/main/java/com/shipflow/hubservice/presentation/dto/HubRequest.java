package com.shipflow.hubservice.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public final class HubRequest {

	private HubRequest() {
	}

	@Getter
	@NoArgsConstructor
	public static class Create {

		@NotBlank
		private String name;

		@NotBlank
		private String address;

		@NotNull
		private BigDecimal latitude;

		@NotNull
		private BigDecimal longitude;

		private UUID managerId;
		
		private String managerName;
	}

	@Getter
	@NoArgsConstructor
	public static class Update {

		private String name;

		private String address;

		private BigDecimal latitude;

		private BigDecimal longitude;

		@NotNull
		private UUID managerId;

		@NotBlank
		private String managerName;
	}
}
