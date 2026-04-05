package com.shipflow.shipmentservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;
import com.shipflow.shipmentservice.application.dto.command.ShipmentManagerCreateCommand;
import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerCreateResult;
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

}
