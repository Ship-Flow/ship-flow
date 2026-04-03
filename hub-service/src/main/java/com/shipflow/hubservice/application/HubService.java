package com.shipflow.hubservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.presentation.dto.HubRequest;
import com.shipflow.hubservice.presentation.dto.HubResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubService {

	private final HubJpaRepository hubRepository;

	public HubResponse.Detail createHub(HubRequest.Create request) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public HubResponse.Detail getHub(UUID hubId) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public List<HubResponse.Summary> getHubs() {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public HubResponse.Detail updateHub(UUID hubId, HubRequest.Update request) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public void deleteHub(UUID hubId) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}
}
