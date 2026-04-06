package com.shipflow.userservice.domain.model;

public enum UserRole {
	MASTER,
	HUB_MANAGER,
	SHIPMENT_MANAGER,
	COMPANY_MANAGER;


	public boolean canReplySignupRequest() {
		return this == MASTER || this == HUB_MANAGER;
	}

	public boolean canUseUserService(){
		return this == MASTER;
	}

	public boolean isHubManager() {
		return this == HUB_MANAGER;
	}

	public boolean isShipmentManager() {
		return this == SHIPMENT_MANAGER;
	}

	public boolean isCompanyManager() {
		return this == COMPANY_MANAGER;
	}
}


