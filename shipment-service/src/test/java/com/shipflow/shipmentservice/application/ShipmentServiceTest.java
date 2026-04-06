package com.shipflow.shipmentservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.dto.result.ShipmentResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteResult;
import com.shipflow.shipmentservice.application.dto.command.ShipmentRouteUpdateCommand;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteUpdateResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentSearchResult;
import com.shipflow.shipmentservice.application.dto.command.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.application.dto.result.ShipmentUpdateResult;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;
import com.shipflow.shipmentservice.domain.ShipmentStatus;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;
import com.shipflow.shipmentservice.fixture.ShipmentFixture;
import com.shipflow.shipmentservice.fixture.ShipmentRouteFixture;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

	@Mock
	private ShipmentRepository shipmentRepository;

	@InjectMocks
	private ShipmentService shipmentService;

	@Nested
	@DisplayName("배송 단건 조회")
	class GetShipmentTest {

		@Test
		@DisplayName("배송 단건 조회 성공")
		void getShipment_success() {
			//given
			UUID shipmentId = UUID.randomUUID();
			Shipment shipment = ShipmentFixture.createShipment(shipmentId);

			given(shipmentRepository.findByIdWithManager(shipmentId))
				.willReturn(Optional.of(shipment));

			//when
			ShipmentResult result = shipmentService.getShipment(shipmentId);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getShipmentId()).isEqualTo(shipmentId);
		}

		@Test
		@DisplayName("배송 정보가 없으면 실패한다")
		void getShipment_notFound() {
			//given
			UUID shipmentId = UUID.randomUUID();
			given(shipmentRepository.findByIdWithManager(shipmentId))
				.willReturn(Optional.empty());

			//when&then
			assertThatThrownBy(() -> shipmentService.getShipment(shipmentId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("배송 수정")
	class UpdateShipmentTest {

		@Test
		@DisplayName("배송 상태 수정 성공")
		void updateShipmentStatus_success() {
			//given
			UUID shipmentId = UUID.randomUUID();
			Shipment shipment = ShipmentFixture.createShipmentWithStatus(shipmentId, ShipmentStatus.WAITING_AT_HUB);
			ShipmentUpdateCommand command = ShipmentUpdateCommand.builder()
				.status(ShipmentStatus.MOVING_TO_HUB)
				.build();

			given(shipmentRepository.findById(shipmentId))
				.willReturn(Optional.of(shipment));

			//when
			ShipmentUpdateResult result = shipmentService.updateShipment(shipmentId, command);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getShipmentId()).isEqualTo(shipmentId);
			assertThat(result.getStatus()).isEqualTo(ShipmentStatus.MOVING_TO_HUB);
		}

		@Test
		@DisplayName("배송 정보가 없으면 실패한다")
		void updateShiopmentStatus_fail_notFound() {
			//given
			UUID shipmentId = UUID.randomUUID();
			ShipmentUpdateCommand command = ShipmentUpdateCommand.builder()
				.status(ShipmentStatus.MOVING_TO_HUB)
				.build();

			given(shipmentRepository.findById(shipmentId))
				.willReturn(Optional.empty());

			//when&then
			assertThatThrownBy(() -> shipmentService.updateShipment(shipmentId, command))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
		}

	}

	@Nested
	@DisplayName("배송 목록 조회")
	class SearchShipmentTest {

		@Test
		@DisplayName("배송 목록 조회 성공")
		void searchShipment_success() {
			//given
			Pageable pageable = PageRequest.of(0, 10);
			Shipment shipment1 = ShipmentFixture.createShipment();
			Shipment shipment2 = ShipmentFixture.createShipment();

			given(shipmentRepository.findAll(pageable))
				.willReturn(List.of(shipment1, shipment2));

			//when
			List<ShipmentSearchResult> result = shipmentService.searchShipment(pageable);

			//then
			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(2);
		}
	}

	@Nested
	@DisplayName("배송 경로 조회")
	class GetShipmentRoutesTest {

		@Test
		@DisplayName("배송 경로 목록 조회 성공")
		void getShipmentRoutes_success() {
			UUID shipmentId = UUID.randomUUID();
			Shipment shipment = ShipmentFixture.createShipment(shipmentId);

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.of(shipment));

			List<ShipmentRouteResult> result = shipmentService.getShipmentRoutes(shipmentId);

			assertThat(result).isNotEmpty();
			assertThat(result).hasSize(2);
		}

		@Test
		@DisplayName("배송 정보가 없으면 실패한다")
		void getShipmentRoutes_notFound() {
			UUID shipmentId = UUID.randomUUID();

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> shipmentService.getShipmentRoutes(shipmentId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("배송 경로 수정")
	class UpdateShipmentRouteTest {

		@Test
		@DisplayName("배송 경로 수정 성공")
		void updateShipmentRoute_toMovingToHub_success() {
			//given
			UUID shipmentId = UUID.randomUUID();
			UUID routeId = UUID.randomUUID();

			ShipmentRoute route = ShipmentRouteFixture.createRoute(routeId, 1);
			Shipment shipment = ShipmentFixture.createShipmentWithRoutes(shipmentId, List.of(route));

			ShipmentRouteUpdateCommand command = ShipmentRouteUpdateCommand.builder()
				.status(ShipmentRouteStatus.MOVING_TO_HUB)
				.build();

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.of(shipment));

			//when
			ShipmentRouteUpdateResult result = shipmentService.updateShipmentRoute(shipmentId, routeId, command);

			//then
			assertThat(result).isNotNull();
			assertThat(result.getShipmentId()).isEqualTo(shipmentId);
			assertThat(result.getShipmentRouteId()).isEqualTo(routeId);
			assertThat(result.getStatus()).isEqualTo(ShipmentRouteStatus.MOVING_TO_HUB);
		}

		@Test
		@DisplayName("지원하지 않는 상태로 경로 수정 시 에러가 발생한다.")
		void updateShipmentRout_fail_unsupportedStatus() {
			//given
			UUID shipmentId = UUID.randomUUID();
			UUID routeId = UUID.randomUUID();

			ShipmentRoute route = ShipmentRouteFixture.createRoute(routeId, 1);
			Shipment shipment = ShipmentFixture.createShipmentWithRoutes(shipmentId, List.of(route));

			ShipmentRouteUpdateCommand command = ShipmentRouteUpdateCommand.builder()
				.status(ShipmentRouteStatus.WAITING_AT_HUB)
				.build();

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.of(shipment));

			//when&then
			assertThatThrownBy(() -> shipmentService.updateShipmentRoute(shipmentId, routeId, command))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
		}

		@Test
		@DisplayName("배송 정보가 없으면 실패한다")
		void updateShipmentRoute_fail_notFound() {
			//given
			UUID shipmentId = UUID.randomUUID();
			UUID routeId = UUID.randomUUID();

			ShipmentRouteUpdateCommand command = ShipmentRouteUpdateCommand.builder()
				.status(ShipmentRouteStatus.MOVING_TO_HUB)
				.build();

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.empty());

			//when&then
			assertThatThrownBy(() -> shipmentService.updateShipmentRoute(shipmentId, routeId, command))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
		}
	}
}
