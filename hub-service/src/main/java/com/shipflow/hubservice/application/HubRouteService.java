package com.shipflow.hubservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shipflow.hubservice.domain.exception.HubErrorCode;
import com.shipflow.hubservice.domain.exception.HubException;
import com.shipflow.hubservice.domain.hub.Hub;
import com.shipflow.hubservice.domain.hub.HubRoute;
import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.infrastructure.persistence.HubRouteJpaRepository;
import com.shipflow.hubservice.presentation.dto.HubRouteRequest;
import com.shipflow.hubservice.presentation.dto.HubRouteResponse;
import com.shipflow.hubservice.presentation.dto.HubRouteSegment;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubRouteService {

	private final HubJpaRepository hubRepository;
	private final HubRouteJpaRepository hubRouteRepository;

	private void requireMaster() {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		String role = (attrs != null) ? attrs.getRequest().getHeader("X-User-Role") : null;
		if (!"MASTER".equals(role)) {
			throw new HubException(HubErrorCode.FORBIDDEN);
		}
	}

	@Transactional
	@CacheEvict(value = "hub-routes", allEntries = true)
	public HubRouteResponse.Detail createRoute(HubRouteRequest.Create request) {
		requireMaster();
		if (request.getDepartureHubId().equals(request.getArrivalHubId())) {
			throw new HubException(HubErrorCode.SAME_SOURCE_DESTINATION);
		}
		Hub departure = hubRepository.findById(request.getDepartureHubId())
			.filter(h -> !h.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
		Hub arrival = hubRepository.findById(request.getArrivalHubId())
			.filter(h -> !h.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
		HubRoute route = HubRoute.builder()
			.departureHub(departure)
			.arrivalHub(arrival)
			.duration(request.getDuration())
			.distance(request.getDistance())
			.build();
		HubRoute saved = hubRouteRepository.save(route);
		return toDetail(saved);
	}

	@Cacheable(value = "hub-route", key = "#routeId")
	public HubRouteResponse.Detail getRoute(UUID routeId) {
		HubRoute route = hubRouteRepository.findById(routeId)
			.filter(r -> !r.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_ROUTE_NOT_FOUND));
		return toDetail(route);
	}

	@Cacheable(value = "hub-routes")
	public List<HubRouteResponse.Summary> getRoutes() {
		return hubRouteRepository.findAllByDeletedAtIsNull().stream()
			.map(this::toSummary)
			.toList();
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "hub-route", key = "#routeId"),
		@CacheEvict(value = "hub-routes", allEntries = true)
	})
	public HubRouteResponse.Detail updateRoute(UUID routeId, HubRouteRequest.Update request) {
		requireMaster();
		HubRoute route = hubRouteRepository.findById(routeId)
			.filter(r -> !r.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_ROUTE_NOT_FOUND));
		route.update(request);
		return toDetail(route);
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "hub-route", key = "#routeId"),
		@CacheEvict(value = "hub-routes", allEntries = true)
	})
	public void deleteRoute(UUID routeId) {
		requireMaster();
		HubRoute route = hubRouteRepository.findById(routeId)
			.filter(r -> !r.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_ROUTE_NOT_FOUND));
		ServletRequestAttributes attrs =
			(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		String userIdStr = (attrs != null) ? attrs.getRequest().getHeader("X-User-Id") : null;
		UUID userId = (userIdStr != null && !userIdStr.isBlank())
			? UUID.fromString(userIdStr)
			: UUID.fromString("00000000-0000-0000-0000-000000000000");
		route.delete(userId);
	}

	@Cacheable(value = "internal-hub-route", key = "#departureHubId + '-' + #arrivalHubId")
	public List<HubRouteSegment> findPath(UUID departureHubId, UUID arrivalHubId) {
		return hubRouteRepository
			.findByDepartureHub_IdAndArrivalHub_IdAndDeletedAtIsNull(departureHubId, arrivalHubId)
			.map(route -> List.of(new HubRouteSegment(
				1,
				route.getDepartureHub().getId(),
				route.getArrivalHub().getId(),
				route.getDistance(),
				route.getDuration()
			)))
			.orElse(List.of());
	}

	private HubRouteResponse.Detail toDetail(HubRoute route) {
		return HubRouteResponse.Detail.builder()
			.id(route.getId())
			.departureHubId(route.getDepartureHub().getId())
			.departureHubName(route.getDepartureHub().getName())
			.arrivalHubId(route.getArrivalHub().getId())
			.arrivalHubName(route.getArrivalHub().getName())
			.duration(route.getDuration())
			.distance(route.getDistance())
			.createdAt(route.getCreatedAt())
			.updatedAt(route.getUpdatedAt())
			.build();
	}

	private HubRouteResponse.Summary toSummary(HubRoute route) {
		return HubRouteResponse.Summary.builder()
			.id(route.getId())
			.departureHubId(route.getDepartureHub().getId())
			.departureHubName(route.getDepartureHub().getName())
			.arrivalHubId(route.getArrivalHub().getId())
			.arrivalHubName(route.getArrivalHub().getName())
			.duration(route.getDuration())
			.distance(route.getDistance())
			.createdAt(route.getCreatedAt())
			.build();
	}
}
