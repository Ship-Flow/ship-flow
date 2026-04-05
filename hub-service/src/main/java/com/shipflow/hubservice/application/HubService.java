package com.shipflow.hubservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shipflow.hubservice.application.event.HubDeletedEvent;
import com.shipflow.hubservice.application.event.HubManagerChangedEvent;
import com.shipflow.hubservice.domain.exception.HubErrorCode;
import com.shipflow.hubservice.domain.exception.HubException;
import com.shipflow.hubservice.domain.hub.Hub;
import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.presentation.dto.HubRequest;
import com.shipflow.hubservice.presentation.dto.HubResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubService {

	private final HubJpaRepository hubRepository;
	private final ApplicationEventPublisher eventPublisher;

	private void requireMaster() {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		String role = (attrs != null) ? attrs.getRequest().getHeader("X-User-Role") : null;
		if (!"MASTER".equals(role)) {
			throw new HubException(HubErrorCode.FORBIDDEN);
		}
	}

	@Transactional
	@CacheEvict(value = "hubs", allEntries = true)
	public HubResponse.Detail createHub(HubRequest.Create request) {
		requireMaster();
		if (hubRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
			throw new HubException(HubErrorCode.DUPLICATE_HUB_NAME);
		}
		Hub hub = Hub.builder()
			.name(request.getName())
			.address(request.getAddress())
			.latitude(request.getLatitude())
			.longitude(request.getLongitude())
			.managerId(request.getManagerId())
			.managerName(request.getManagerName())
			.build();
		Hub saved = hubRepository.save(hub);
		return toDetail(saved);
	}

	@Cacheable(value = "hub", key = "#hubId")
	public HubResponse.Detail getHub(UUID hubId) {
		Hub hub = hubRepository.findById(hubId)
			.filter(h -> !h.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
		return toDetail(hub);
	}

	@Cacheable(value = "hubs")
	public List<HubResponse.Summary> getHubs() {
		return hubRepository.findAllByDeletedAtIsNull().stream()
			.map(this::toSummary)
			.toList();
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "hub", key = "#hubId"),
		@CacheEvict(value = "hubs", allEntries = true)
	})
	public HubResponse.Detail updateHub(UUID hubId, HubRequest.Update request) {
		requireMaster();
		Hub hub = hubRepository.findById(hubId)
			.filter(h -> !h.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
		UUID oldManagerId = hub.getManagerId();
		hub.update(request);
		UUID newManagerId = hub.getManagerId();
		if (!oldManagerId.equals(newManagerId)) {
			UUID requestUserId = extractRequestUserId();
			eventPublisher.publishEvent(
				new HubManagerChangedEvent(oldManagerId, newManagerId, hubId, requestUserId));
		}
		return toDetail(hub);
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "hub", key = "#hubId"),
		@CacheEvict(value = "hubs", allEntries = true)
	})
	public void deleteHub(UUID hubId) {
		requireMaster();
		Hub hub = hubRepository.findById(hubId)
			.filter(h -> !h.isDeleted())
			.orElseThrow(() -> new HubException(HubErrorCode.HUB_NOT_FOUND));
		UUID userId = extractRequestUserId();
		UUID managerId = hub.getManagerId();
		hub.delete(userId);
		eventPublisher.publishEvent(new HubDeletedEvent(hubId, managerId, userId));
	}

	private UUID extractRequestUserId() {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		String userIdStr = (attrs != null) ? attrs.getRequest().getHeader("X-User-Id") : null;
		return (userIdStr != null && !userIdStr.isBlank())
			? UUID.fromString(userIdStr)
			: UUID.fromString("00000000-0000-0000-0000-000000000000");
	}

	private HubResponse.Detail toDetail(Hub hub) {
		return HubResponse.Detail.builder()
			.id(hub.getId())
			.name(hub.getName())
			.address(hub.getAddress())
			.latitude(hub.getLatitude())
			.longitude(hub.getLongitude())
			.managerId(hub.getManagerId())
			.managerName(hub.getManagerName())
			.createdAt(hub.getCreatedAt())
			.updatedAt(hub.getUpdatedAt())
			.build();
	}

	private HubResponse.Summary toSummary(Hub hub) {
		return HubResponse.Summary.builder()
			.id(hub.getId())
			.name(hub.getName())
			.address(hub.getAddress())
			.latitude(hub.getLatitude())
			.longitude(hub.getLongitude())
			.managerId(hub.getManagerId())
			.managerName(hub.getManagerName())
			.createdAt(hub.getCreatedAt())
			.build();
	}
}
