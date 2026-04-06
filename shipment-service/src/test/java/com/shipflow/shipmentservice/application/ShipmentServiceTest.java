package com.shipflow.shipmentservice.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.application.client.CacheClient;
import com.shipflow.shipmentservice.application.client.HubClient;
import com.shipflow.shipmentservice.application.client.UserClient;
import com.shipflow.shipmentservice.application.client.dto.HubRouteResult;
import com.shipflow.shipmentservice.application.client.dto.UserInfo;
import com.shipflow.shipmentservice.application.dto.command.CreateShipmentCommand;
import com.shipflow.shipmentservice.application.dto.command.ShipmentRouteUpdateCommand;
import com.shipflow.shipmentservice.application.dto.command.ShipmentUpdateCommand;
import com.shipflow.shipmentservice.application.dto.result.ShipmentCanceledResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentCompleteResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentRouteUpdateResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentSearchResult;
import com.shipflow.shipmentservice.application.dto.result.ShipmentUpdateResult;
import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;
import com.shipflow.shipmentservice.domain.ShipmentStatus;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;
import com.shipflow.shipmentservice.domain.repository.ShipmentManagerRepository;
import com.shipflow.shipmentservice.domain.repository.ShipmentRepository;
import com.shipflow.shipmentservice.fixture.ShipmentFixture;
import com.shipflow.shipmentservice.fixture.ShipmentManagerFixture;
import com.shipflow.shipmentservice.fixture.ShipmentRouteFixture;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock
	private ShipmentManagerRepository shipmentManagerRepository;
	@Mock
	private HubClient hubClient;
	@Mock
	private UserClient userClient;
	@Mock
	private CacheClient cacheClient;
	@Mock
	private ShipmentEventPublisher eventPublisher;

	@InjectMocks
	private ShipmentService shipmentService;

	private HubRouteResult createHubRouteResult(int sequence, UUID departureHubId, UUID arrivalHubId) {
		HubRouteResult result = new HubRouteResult();
		ReflectionTestUtils.setField(result, "sequence", sequence);
		ReflectionTestUtils.setField(result, "departureHubId", departureHubId);
		ReflectionTestUtils.setField(result, "arrivalHubId", arrivalHubId);
		ReflectionTestUtils.setField(result, "estimatedDistance", new BigDecimal("12.50"));
		ReflectionTestUtils.setField(result, "estimatedDuration", 30);
		return result;
	}

	@Nested
	@DisplayName("배송 생성")
	class CreateShipmentTest {

		@Test
		@DisplayName("배송 생성 성공")
		void createShipment_success() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID ordererId = UUID.randomUUID();
			UUID departureHubId = UUID.randomUUID();
			UUID arrivalHubId = UUID.randomUUID();

			CreateShipmentCommand command = new CreateShipmentCommand(
				orderId, ordererId, UUID.randomUUID(), 5,
				departureHubId, arrivalHubId,
				LocalDateTime.now().plusDays(7), "요청사항", "서울시 강남구 테헤란로 123"
			);

			ShipmentManager companyManager = ShipmentManagerFixture.createCompanyManager();
			ShipmentManager hubManager = ShipmentManagerFixture.createHubManager();
			HubRouteResult hubRoute = createHubRouteResult(1, departureHubId, arrivalHubId);
			UserInfo userInfo = new UserInfo(ordererId, "홍길동", "hong123");
			Shipment savedShipment = ShipmentFixture.createShipment();

			given(shipmentManagerRepository.findFirstAvailableByType(ShipmentManagerType.COMPANY))
				.willReturn(Optional.of(companyManager));
			given(hubClient.getHubRoutes(departureHubId, arrivalHubId))
				.willReturn(List.of(hubRoute));
			given(shipmentManagerRepository.findAllByType(ShipmentManagerType.HUB))
				.willReturn(List.of(hubManager));
			given(userClient.getUser(ordererId))
				.willReturn(userInfo);
			given(cacheClient.increment(anyString()))
				.willReturn(1L);
			given(shipmentRepository.save(any()))
				.willReturn(savedShipment);

			// when & then
			assertThatNoException().isThrownBy(() -> shipmentService.createShipment(command));
			then(eventPublisher).should().publishCreated(any());
		}

		@Test
		@DisplayName("업체 배송 담당자가 없으면 생성에 실패하고 실패 이벤트를 발행한다")
		void createShipment_companyManagerNotFound() {
			// given
			CreateShipmentCommand command = new CreateShipmentCommand(
				UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5,
				UUID.randomUUID(), UUID.randomUUID(),
				LocalDateTime.now().plusDays(7), "요청사항", "서울시 강남구 테헤란로 123"
			);

			given(shipmentManagerRepository.findFirstAvailableByType(ShipmentManagerType.COMPANY))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> shipmentService.createShipment(command))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND);

			then(eventPublisher).should().publishCreationFailed(any());
		}

		@Test
		@DisplayName("허브 배송 담당자가 없으면 생성에 실패하고 실패 이벤트를 발행한다")
		void createShipment_hubManagerNotFound() {
			// given
			UUID departureHubId = UUID.randomUUID();
			UUID arrivalHubId = UUID.randomUUID();

			CreateShipmentCommand command = new CreateShipmentCommand(
				UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5,
				departureHubId, arrivalHubId,
				LocalDateTime.now().plusDays(7), "요청사항", "서울시 강남구 테헤란로 123"
			);

			given(shipmentManagerRepository.findFirstAvailableByType(ShipmentManagerType.COMPANY))
				.willReturn(Optional.of(ShipmentManagerFixture.createCompanyManager()));
			given(hubClient.getHubRoutes(departureHubId, arrivalHubId))
				.willReturn(List.of(createHubRouteResult(1, departureHubId, arrivalHubId)));
			given(shipmentManagerRepository.findAllByType(ShipmentManagerType.HUB))
				.willReturn(Collections.emptyList());

			// when & then
			assertThatThrownBy(() -> shipmentService.createShipment(command))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_MANAGER_NOT_FOUND);

			then(eventPublisher).should().publishCreationFailed(any());
		}
	}

	@Nested
	@DisplayName("배송 완료")
	class CompleteShipmentTest {

		@Test
		@DisplayName("모든 경로가 완료되면 배송 완료 성공")
		void completeShipment_success() {
			// given
			UUID shipmentId = UUID.randomUUID();
			List<ShipmentRoute> arrivedRoutes = List.of(
				ShipmentRouteFixture.createArrivedRoute(UUID.randomUUID(), 1, LocalDateTime.now().minusHours(2)),
				ShipmentRouteFixture.createArrivedRoute(UUID.randomUUID(), 2, LocalDateTime.now().minusHours(1))
			);
			Shipment shipment = ShipmentFixture.createShipmentWithRoutes(shipmentId, arrivedRoutes);

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.of(shipment));

			// when
			ShipmentCompleteResult result = shipmentService.completeShipment(shipmentId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getShipmentId()).isEqualTo(shipmentId);
			then(eventPublisher).should().publishCompleted(any());
		}

		@Test
		@DisplayName("배송 정보가 없으면 실패한다")
		void completeShipment_notFound() {
			// given
			UUID shipmentId = UUID.randomUUID();
			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> shipmentService.completeShipment(shipmentId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
		}

		@Test
		@DisplayName("이미 완료된 배송이면 실패한다")
		void completeShipment_alreadyCompleted() {
			// given
			UUID shipmentId = UUID.randomUUID();
			Shipment shipment = ShipmentFixture.createShipmentWithStatus(shipmentId, ShipmentStatus.COMPLETED);

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.of(shipment));

			// when & then
			assertThatThrownBy(() -> shipmentService.completeShipment(shipmentId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_ALREADY_COMPLETED);
		}

		@Test
		@DisplayName("완료되지 않은 경로가 있으면 실패한다")
		void completeShipment_routesNotAllCompleted() {
			// given
			UUID shipmentId = UUID.randomUUID();
			List<ShipmentRoute> routes = List.of(
				ShipmentRouteFixture.createArrivedRoute(UUID.randomUUID(), 1, LocalDateTime.now().minusHours(1)),
				ShipmentRouteFixture.createRoute(UUID.randomUUID(), 2)  // WAITING_AT_HUB
			);
			Shipment shipment = ShipmentFixture.createShipmentWithRoutes(shipmentId, routes);

			given(shipmentRepository.findByIdWithRoutes(shipmentId))
				.willReturn(Optional.of(shipment));

			// when & then
			assertThatThrownBy(() -> shipmentService.completeShipment(shipmentId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_ROUTES_NOT_ALL_COMPLETED);
		}
	}

	@Nested
	@DisplayName("배송 취소")
	class CancelShipmentTest {

		@Test
		@DisplayName("WAITING_AT_HUB 상태의 배송은 취소 성공")
		void cancelShipment_success() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID shipmentId = UUID.randomUUID();
			List<ShipmentRoute> routes = List.of(
				ShipmentRouteFixture.createRoute(UUID.randomUUID(), 1),
				ShipmentRouteFixture.createRoute(UUID.randomUUID(), 2)
			);
			Shipment shipment = ShipmentFixture.createShipmentWithRoutes(shipmentId, routes);

			given(shipmentRepository.findByOrderIdWithRoutes(orderId))
				.willReturn(Optional.of(shipment));

			// when
			ShipmentCanceledResult result = shipmentService.cancelShipment(orderId);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getShipmentId()).isEqualTo(shipmentId);
			assertThat(result.getStatus()).isEqualTo(ShipmentStatus.CANCELLED);
		}

		@Test
		@DisplayName("배송 정보가 없으면 실패한다")
		void cancelShipment_notFound() {
			// given
			UUID orderId = UUID.randomUUID();
			given(shipmentRepository.findByOrderIdWithRoutes(orderId))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> shipmentService.cancelShipment(orderId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_FOUND);
		}

		@Test
		@DisplayName("이미 취소된 배송이면 실패한다")
		void cancelShipment_alreadyCancelled() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID shipmentId = UUID.randomUUID();
			Shipment shipment = ShipmentFixture.createShipmentWithStatus(shipmentId, ShipmentStatus.CANCELLED);

			given(shipmentRepository.findByOrderIdWithRoutes(orderId))
				.willReturn(Optional.of(shipment));

			// when & then
			assertThatThrownBy(() -> shipmentService.cancelShipment(orderId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_ALREADY_CANCELLED);
		}

		@Test
		@DisplayName("배송이 이미 시작된 경우 취소 실패")
		void cancelShipment_notCancelableStatus() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID shipmentId = UUID.randomUUID();
			Shipment shipment = ShipmentFixture.createShipmentWithStatus(shipmentId, ShipmentStatus.MOVING_TO_HUB);

			given(shipmentRepository.findByOrderIdWithRoutes(orderId))
				.willReturn(Optional.of(shipment));

			// when & then
			assertThatThrownBy(() -> shipmentService.cancelShipment(orderId))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode")
				.isEqualTo(ShipmentErrorCode.SHIPMENT_NOT_CANCELABLE_STATUS);
		}
	}

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
