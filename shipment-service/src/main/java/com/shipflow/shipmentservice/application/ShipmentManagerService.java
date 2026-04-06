package com.shipflow.shipmentservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;
import com.shipflow.shipmentservice.application.dto.command.ShipmentManagerCreateCommand;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerCreateResult;
import com.shipflow.shipmentservice.application.dto.query.ShipmentManagerSearchQuery;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerSearchResult;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;
import com.shipflow.shipmentservice.domain.repository.ShipmentManagerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentManagerService {

	private final ShipmentManagerRepository shipmentManagerRepository;
	private final UserClient userClient;

	@Transactional
	public ShipmentManagerCreateResult createShipmentManager(ShipmentManagerCreateCommand command) {
		UserInfo user = userClient.getUser(command.getUserId());

		int nextSequence = getNextSequence(command);

		ShipmentManager shipmentManager = createShipmentManager(command, user, nextSequence);

		shipmentManagerRepository.save(shipmentManager);

		return ShipmentManagerCreateResult.fromEntity(shipmentManager);
	}

	private int getNextSequence(ShipmentManagerCreateCommand command) {
		ShipmentManagerType type = command.getType();

		if (type == ShipmentManagerType.COMPANY) {
			if (command.getHubId() == null) {
				throw new BusinessException(ShipmentErrorCode.HUB_ID_REQUIRED_FOR_COMPANY_MANAGER);
			}

			return shipmentManagerRepository.findMaxSequenceByTypeAndHubId(
				type,
				command.getHubId()
			) + 1;
		}

		return shipmentManagerRepository.findMaxSequenceByType(type) + 1;
	}

	private ShipmentManager createShipmentManager(
		ShipmentManagerCreateCommand command,
		UserInfo user,
		int sequence
	) {
		if (command.getType() == ShipmentManagerType.COMPANY) {
			return ShipmentManager.createCompanyManager(
				command.getUserId(),
				user.getName(),
				command.getHubId(),
				user.getSlackId(),
				sequence
			);
		}

		if (command.getType() == ShipmentManagerType.HUB) {
			return ShipmentManager.createHubManager(
				command.getUserId(),
				user.getName(),
				user.getSlackId(),
				sequence
			);
		}

		throw new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_TYPE_REQUIRED);
	}

	public List<ShipmentManagerSearchResult> searchShipmentManager(ShipmentManagerSearchQuery query,
		Pageable pageable) {
		return shipmentManagerRepository.findAll(query, pageable).stream()
			.map(ShipmentManagerSearchResult::fromEntity)
			.toList();
	}

	public ShipmentManagerResult getShipmentManager(UUID managerId) {
		ShipmentManager shipmentManager = shipmentManagerRepository.findById(managerId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND));
		return ShipmentManagerResult.fromEntity(shipmentManager);
	}

	@Transactional
	public void deleteShipmentManager(UUID managerId, UUID userId) {
		ShipmentManager shipmentManager = shipmentManagerRepository.findById(managerId)
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND));

		shipmentManager.delete(userId);
	}
}
