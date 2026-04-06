package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.shipflow.shipmentservice.application.dto.query.ShipmentManagerSearchQuery;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;
import com.shipflow.shipmentservice.domain.repository.ShipmentManagerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipmentManagerRepositoryImpl implements ShipmentManagerRepository {

	private final ShipmentManagerJpaRepository shipmentManagerJpaRepository;
	private final ShipmentManagerCustomRepository shipmentManagerCustomRepository;

	@Override
	public ShipmentManager save(ShipmentManager shipmentManager) {
		return shipmentManagerJpaRepository.save(shipmentManager);
	}

	@Override
	public Optional<ShipmentManager> findById(UUID managerId) {
		return shipmentManagerJpaRepository.findByIdAndDeletedAtIsNull(managerId);
	}

	@Override
	public List<ShipmentManager> findAll(ShipmentManagerSearchQuery query, Pageable pageable) {
		return shipmentManagerCustomRepository.search(query, pageable);
	}

	@Override
	public int findMaxSequenceByType(ShipmentManagerType type) {
		return shipmentManagerJpaRepository.findMaxSequenceByType(type);
	}

	@Override
	public int findMaxSequenceByTypeAndHubId(ShipmentManagerType type, UUID hubId) {
		return shipmentManagerJpaRepository.findMaxSequenceByTypeAndHubId(type, hubId);
	}

	@Override
	public Optional<ShipmentManager> findFirstAvailableByType(ShipmentManagerType type) {
		return shipmentManagerJpaRepository.findFirstAvailableByType(type);
	}

	@Override
	public List<ShipmentManager> findAllByType(ShipmentManagerType type) {
		return shipmentManagerJpaRepository.findAllByType(type);
	}
}
