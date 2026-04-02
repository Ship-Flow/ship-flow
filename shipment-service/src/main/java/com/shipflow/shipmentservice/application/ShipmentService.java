package com.shipflow.shipmentservice.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shipflow.shipmentservice.application.dto.ShipmentResult;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentService {

	private final ShipmentRepository shipmentRepository;

	public ShipmentResult getShipment(UUID shipmentId) {
		Shipment shipment = shipmentRepository.findById(shipmentId)
			.orElseThrow(() -> new IllegalArgumentException("배송 정보가 존재하지 않습니다."));

		return ShipmentResult.fromEntity(shipment);
	}
}
