package com.shipflow.shipmentservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shipflow.shipmentservice.application.dto.ShipmentResult;
import com.shipflow.shipmentservice.application.dto.ShipmentSearchResult;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
}
