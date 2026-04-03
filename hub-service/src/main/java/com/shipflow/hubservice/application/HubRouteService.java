package com.shipflow.hubservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.infrastructure.persistence.HubRouteJpaRepository;
import com.shipflow.hubservice.presentation.dto.HubRouteRequest;
import com.shipflow.hubservice.presentation.dto.HubRouteResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubRouteService {

	private final HubJpaRepository hubRepository;
	private final HubRouteJpaRepository hubRouteRepository;

	public HubRouteResponse.Detail createRoute(HubRouteRequest.Create request) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public HubRouteResponse.Detail getRoute(UUID routeId) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public List<HubRouteResponse.Summary> getRoutes() {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public HubRouteResponse.Detail updateRoute(UUID routeId, HubRouteRequest.Update request) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}

	public void deleteRoute(UUID routeId) {
		throw new UnsupportedOperationException("Phase 4에서 구현");
	}
}
