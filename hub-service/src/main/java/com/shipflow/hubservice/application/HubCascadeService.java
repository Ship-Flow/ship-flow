package com.shipflow.hubservice.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shipflow.hubservice.infrastructure.client.CompanyClient;
import com.shipflow.hubservice.infrastructure.client.DeliveryClient;
import com.shipflow.hubservice.infrastructure.client.UserClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubCascadeService {

	private final UserClient userClient;
	private final DeliveryClient deliveryClient;
	private final CompanyClient companyClient;

	public void cascadeDeleteHub(UUID hubId) {
		throw new UnsupportedOperationException("Phase 5에서 구현");
	}
}
