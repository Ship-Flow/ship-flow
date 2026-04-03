package com.shipflow.shipmentservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.shipmentservice.application.dto.ShipmentResult;
import com.shipflow.shipmentservice.application.dto.ShipmentSearchResult;
import com.shipflow.shipmentservice.application.dto.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.application.dto.ShipmentUpdateResult;
import com.shipflow.shipmentservice.domain.Shipment;
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
		Shipment shipment = shipmentRepository.findById(shipmentId)
			.orElseThrow(() -> new IllegalArgumentException("배송 정보가 존재하지 않습니다."));

		return ShipmentResult.fromEntity(shipment);
	}

	@Transactional
	public ShipmentUpdateResult updateShipment(UUID shipmentId, ShipmentUpdateCommand command) {
		Shipment shipment = shipmentRepository.findById(shipmentId)
			.orElseThrow(() -> new IllegalArgumentException("배송 정보가 존재하지 않습니다."));

		shipment.updateStatus(command.getStatus());

		return ShipmentUpdateResult.fromEntity(shipment);
	}
}
