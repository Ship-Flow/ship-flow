package com.shipflow.shipmentservice.application;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.CacheClient;
import com.shipflow.shipmentservice.application.client.HubClient;
import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.HubRouteResult;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;
import com.shipflow.shipmentservice.application.dto.command.CreateShipmentCommand;
import com.shipflow.shipmentservice.application.dto.command.ShipmentRouteUpdateCommand;
import com.shipflow.shipmentservice.application.dto.command.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.application.dto.result.ShipmentCanceledResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentCompleteResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteUpdateResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentSearchResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentUpdateResult;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.event.ShipmentCompletedEvent;
import com.shipflow.shipmentservice.domain.event.ShipmentCreatedEvent;
import com.shipflow.shipmentservice.domain.event.ShipmentCreationFailedEvent;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;
import com.shipflow.shipmentservice.domain.repository.ShipmentManagerRepository;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentService {

	private static final String HUB_MANAGER_CURSOR_KEY = "shipment:hub_manager:cursor";

	private final ShipmentRepository shipmentRepository;
	private final ShipmentManagerRepository shipmentManagerRepository;
	private final HubClient hubClient;
	private final UserClient userClient;
	private final CacheClient cacheClient;
	private final ShipmentEventPublisher eventPublisher;

	@Transactional
	public void createShipment(CreateShipmentCommand command) {
		try {
			ShipmentManager companyManager = findCompanyManager();
			List<HubRouteResult> hubRoutes = hubClient.getHubRoutes(command.departureHubId(), command.arrivalHubId());
			List<ShipmentManager> hubManagers = findHubManagers();

			UserInfo recipient = userClient.getUser(command.ordererId());

			List<ShipmentRoute> routes = buildRoutes(hubRoutes, hubManagers);

			Shipment shipment = Shipment.create(
				command.orderId(),
				command.departureHubId(),
				command.arrivalHubId(),
				command.shipmentAddress(),
				recipient.getName(),
				recipient.getSlackId(),
				companyManager,
				routes
			);

			Shipment saved = shipmentRepository.save(shipment);

			List<ShipmentCreatedEvent.Route> eventRoutes = saved.getRoutes().stream()
				.map(r -> new ShipmentCreatedEvent.Route(r.getSequence(), r.getDepartureHubId(), r.getArrivalHubId()))
				.toList();

			eventPublisher.publishCreated(new ShipmentCreatedEvent(
				saved.getOrderId(),
				saved.getId(),
				command.productId(),
				command.quantity(),
				saved.getDepartureHubId(),
				saved.getArrivalHubId(),
				command.requestDeadline(),
				command.requestNote(),
				eventRoutes
			));

			log.info("[Shipment] Created | shipmentId={} | orderId={}", saved.getId(), saved.getOrderId());

		} catch (Exception e) {
			log.error("[Shipment] Creation failed | orderId={} | error={}", command.orderId(), e.getMessage(), e);
			eventPublisher.publishCreationFailed(new ShipmentCreationFailedEvent(command.orderId()));
			throw e;
		}
	}

	public List<ShipmentSearchResult> searchShipment(Pageable pageable) {
		return shipmentRepository.findAll(pageable).stream()
			.map(ShipmentSearchResult::fromEntity)
			.toList();
	}

	public ShipmentResult getShipment(UUID shipmentId) {
		Shipment shipment = shipmentRepository.findByIdWithManager(shipmentId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_NOT_FOUND));

		return ShipmentResult.fromEntity(shipment);
	}

	@Transactional
	public ShipmentUpdateResult updateShipment(UUID shipmentId, ShipmentUpdateCommand command) {
		Shipment shipment = shipmentRepository.findById(shipmentId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_NOT_FOUND));

		shipment.updateStatus(command.getStatus());

		return ShipmentUpdateResult.fromEntity(shipment);
	}

	public List<ShipmentRouteResult> getShipmentRoutes(UUID shipmentId) {
		Shipment shipment = shipmentRepository.findByIdWithRoutes(shipmentId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_NOT_FOUND));

		return shipment.getRoutes().stream()
			.map(ShipmentRouteResult::fromEntity)
			.toList();
	}

	@Transactional
	public ShipmentRouteUpdateResult updateShipmentRoute(UUID shipmentId, UUID routeId,
		ShipmentRouteUpdateCommand command) {
		Shipment shipment = shipmentRepository.findByIdWithRoutes(shipmentId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_NOT_FOUND));

		ShipmentRoute route;

		switch (command.getStatus()) {
			case MOVING_TO_HUB -> route = shipment.markRouteMovingToHub(routeId);
			case ARRIVED_AT_HUB -> route = shipment.markRouteArrivedAtHub(routeId, command.getActualDistance());
			default -> throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
		}

		return ShipmentRouteUpdateResult.fromEntity(route);
	}

	@Transactional
	public ShipmentCompleteResult completeShipment(UUID shipmentId) {
		Shipment shipment = shipmentRepository.findByIdWithRoutes(shipmentId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_NOT_FOUND));

		shipment.markCompleted();

		eventPublisher.publishCompleted(new ShipmentCompletedEvent(
			shipment.getOrderId(),
			shipment.getId()
		));

		return ShipmentCompleteResult.fromEntity(shipment);
	}

	@Transactional
	public ShipmentCanceledResult cancelShipment(UUID orderId) {
		Shipment shipment = shipmentRepository.findByOrderIdWithRoutes(orderId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_NOT_FOUND));

		shipment.markCanceled();

		return ShipmentCanceledResult.fromEntity(shipment);
	}

	private ShipmentManager findCompanyManager() {
		return shipmentManagerRepository.findFirstAvailableByType(ShipmentManagerType.COMPANY)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND));
	}

	private List<ShipmentManager> findHubManagers() {
		List<ShipmentManager> managers = shipmentManagerRepository.findAllByType(ShipmentManagerType.HUB);
		if (managers.isEmpty()) {
			throw new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND);
		}
		return managers;
	}

	private List<ShipmentRoute> buildRoutes(List<HubRouteResult> hubRoutes, List<ShipmentManager> hubManagers) {
		long cursor = cacheClient.increment(HUB_MANAGER_CURSOR_KEY);
		int startIndex = (int)((cursor - 1) % hubManagers.size());

		return hubRoutes.stream()
			.sorted(Comparator.comparingInt(HubRouteResult::getSequence))
			.map(route -> {
				int managerIndex = (startIndex + route.getSequence() - 1) % hubManagers.size();
				ShipmentManager assignedManager = hubManagers.get(managerIndex);
				return ShipmentRoute.create(
					route.getSequence(),
					route.getDepartureHubId(),
					route.getArrivalHubId(),
					route.getEstimatedDistance(),
					route.getEstimatedDuration(),
					assignedManager
				);
			})
			.toList();
	}
}
