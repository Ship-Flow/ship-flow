package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryImpl implements ShipmentRepository {

	private final ShipmentJpaRepository shipmentJpaRepository;

	@Override
	public Optional<Shipment> findById(UUID shipmentId) {
		return shipmentJpaRepository.findById(shipmentId);
	}
}
