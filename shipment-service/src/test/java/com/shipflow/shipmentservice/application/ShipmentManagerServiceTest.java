package com.shipflow.shipmentservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;
import com.shipflow.shipmentservice.application.dto.command.ShipmentManagerCreateCommand;
import com.shipflow.shipmentservice.application.dto.query.ShipmentManagerSearchQuery;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerCreateResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerSearchResult;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;
import com.shipflow.shipmentservice.domain.repository.ShipmentManagerRepository;
import com.shipflow.shipmentservice.fixture.ShipmentManagerFixture;

@ExtendWith(MockitoExtension.class)
class ShipmentManagerServiceTest {

	@Mock
	private ShipmentManagerRepository shipmentManagerRepository;

	@Mock
	private UserClient userClient;

	@InjectMocks
	private ShipmentManagerService shipmentManagerService;

	@Nested
	@DisplayName("배송 담당자 생성")
	class CreateShipmentManager {

		@Test
		void createCompanyManager_success() {
			//given
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			ShipmentManagerCreateCommand command = ShipmentManagerCreateCommand.builder()
				.userId(userId)
				.hubId(hubId)
				.type(ShipmentManagerType.COMPANY)
				.build();

			UserInfo userInfo = new UserInfo(userId, "김업체", "company123");

			given(userClient.getUser(command.getUserId()))
				.willReturn(userInfo);
			given(shipmentManagerRepository.findMaxSequenceByTypeAndHubId(command.getType(), command.getHubId()))
				.willReturn(2);

			//when
			ShipmentManagerCreateResult result = shipmentManagerService.createShipmentManager(command);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getUserId()).isEqualTo(command.getUserId());
			assertThat(result.getHubId()).isEqualTo(command.getHubId());
			assertThat(result.getType()).isEqualTo(ShipmentManagerType.COMPANY);
			assertThat(result.getShipmentSequence()).isEqualTo(3);
		}

		@Test
		@DisplayName("허브 배송 담당자를 생성할 수 있다")
		void createHubManager_success() {
			// given
			UUID userId = UUID.randomUUID();
			ShipmentManagerCreateCommand command = ShipmentManagerCreateCommand.builder()
				.userId(userId)
				.type(ShipmentManagerType.HUB)
				.build();

			UserInfo userInfo = new UserInfo(userId, "김허브", "hub123");

			given(userClient.getUser(command.getUserId())).willReturn(userInfo);
			given(shipmentManagerRepository.findMaxSequenceByType(command.getType()))
				.willReturn(0);

			// when
			ShipmentManagerCreateResult result = shipmentManagerService.createShipmentManager(command);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getUserId()).isEqualTo(command.getUserId());
			assertThat(result.getHubId()).isNull();
			assertThat(result.getType()).isEqualTo(ShipmentManagerType.HUB);
			assertThat(result.getShipmentSequence()).isEqualTo(1);
		}

		@Test
		@DisplayName("업체 배송 담당자 생성 시 hubId가 없으면 예외가 발생한다")
		void createCompanyManager_fail_whenHubIdNull() {
			// given
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			ShipmentManagerCreateCommand command = ShipmentManagerCreateCommand.builder()
				.userId(userId)
				.type(ShipmentManagerType.COMPANY)
				.build();

			UserInfo userInfo = new UserInfo(userId, "김업체", "company123");

			given(userClient.getUser(command.getUserId())).willReturn(userInfo);

			// when & then
			assertThatThrownBy(() -> shipmentManagerService.createShipmentManager(command))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.HUB_ID_REQUIRED_FOR_COMPANY_MANAGER);

			then(shipmentManagerRepository).should(never()).save(any());
		}
	}

	@Nested
	@DisplayName("배송 담당자 단건 조회")
	class GetShipmentManager {

		@Test
		@DisplayName("존재하는 담당자를 조회할 수 있다")
		void getShipmentManager_success() {
			// given
			ShipmentManager manager = ShipmentManagerFixture.createHubManager();

			given(shipmentManagerRepository.findById(manager.getId()))
				.willReturn(Optional.of(manager));

			// when
			ShipmentManagerResult result = shipmentManagerService.getShipmentManager(manager.getId());

			// then
			assertThat(result).isNotNull();
			assertThat(result.getShipmentManagerId()).isEqualTo(manager.getId());
			assertThat(result.getType()).isEqualTo(ShipmentManagerType.HUB);
		}

		@Test
		@DisplayName("존재하지 않는 담당자 조회 시 예외가 발생한다")
		void getShipmentManager_fail_whenNotFound() {
			// given
			UUID managerId = UUID.randomUUID();

			given(shipmentManagerRepository.findById(managerId))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> shipmentManagerService.getShipmentManager(managerId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("배송 담당자 목록 조회")
	class SearchShipmentManager {

		@Test
		@DisplayName("타입 조건 없이 전체 담당자 목록을 조회할 수 있다")
		void searchShipmentManager_withoutFilter() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			ShipmentManagerSearchQuery query = ShipmentManagerSearchQuery.builder().build();

			List<ShipmentManager> managers = List.of(
				ShipmentManagerFixture.createHubManager(),
				ShipmentManagerFixture.createCompanyManager()
			);

			given(shipmentManagerRepository.findAll(query, pageable)).willReturn(managers);

			// when
			List<ShipmentManagerSearchResult> result = shipmentManagerService.searchShipmentManager(query, pageable);

			// then
			assertThat(result).hasSize(2);
		}

		@Test
		@DisplayName("타입으로 필터링하여 담당자 목록을 조회할 수 있다")
		void searchShipmentManager_withTypeFilter() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			ShipmentManagerSearchQuery query = ShipmentManagerSearchQuery.builder()
				.type(ShipmentManagerType.HUB)
				.build();

			List<ShipmentManager> managers = List.of(
				ShipmentManagerFixture.createHubManager(),
				ShipmentManagerFixture.createHubManager()
			);

			given(shipmentManagerRepository.findAll(query, pageable)).willReturn(managers);

			// when
			List<ShipmentManagerSearchResult> result = shipmentManagerService.searchShipmentManager(query, pageable);

			// then
			assertThat(result).hasSize(2);
			assertThat(result).allMatch(r -> r.getType() == ShipmentManagerType.HUB);
		}
	}

	@Nested
	@DisplayName("배송 담당자 삭제")
	class DeleteShipmentManager {

		@Test
		@DisplayName("담당자를 소프트 딜리트할 수 있다")
		void deleteShipmentManager_success() {
			// given
			UUID userId = UUID.randomUUID();
			ShipmentManager manager = ShipmentManagerFixture.createHubManager();

			given(shipmentManagerRepository.findById(manager.getId()))
				.willReturn(Optional.of(manager));

			// when
			shipmentManagerService.deleteShipmentManager(manager.getId(), userId);

			// then
			assertThat(ReflectionTestUtils.getField(manager, "deletedAt")).isNotNull();
			assertThat(ReflectionTestUtils.getField(manager, "deletedBy")).isEqualTo(userId);
		}

		@Test
		@DisplayName("존재하지 않는 담당자 삭제 시 예외가 발생한다")
		void deleteShipmentManager_fail_whenNotFound() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			given(shipmentManagerRepository.findById(managerId))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> shipmentManagerService.deleteShipmentManager(managerId, userId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND);
		}
	}

}
