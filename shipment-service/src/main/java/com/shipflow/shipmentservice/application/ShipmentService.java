package com.shipflow.shipmentservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.dto.ShipmentResult;
import com.shipflow.shipmentservice.application.dto.ShipmentRouteResult;
import com.shipflow.shipmentservice.application.dto.ShipmentRouteUpdateCommand;
import com.shipflow.shipmentservice.application.dto.ShipmentRouteUpdateResult;
import com.shipflow.shipmentservice.application.dto.ShipmentSearchResult;
import com.shipflow.shipmentservice.application.dto.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.application.dto.ShipmentUpdateResult;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentService {

	private final ShipmentRepository shipmentRepository;

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

			case MOVING_TO_HUB -> {
				route = shipment.markRouteMovingToHub(routeId);
			}

			case ARRIVED_AT_HUB -> {
				route = shipment.markRouteArrivedAtHub(
					routeId,
					command.getActualDistance()
				);
			}

			default -> {
				throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
			}
		}

		return ShipmentRouteUpdateResult.fromEntity(route);
	}
}
