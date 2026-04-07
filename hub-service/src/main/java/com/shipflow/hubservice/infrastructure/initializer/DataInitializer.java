package com.shipflow.hubservice.infrastructure.initializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.hubservice.domain.hub.Hub;
import com.shipflow.hubservice.domain.hub.HubRoute;
import com.shipflow.hubservice.infrastructure.persistence.HubJpaRepository;
import com.shipflow.hubservice.infrastructure.persistence.HubRouteJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

	private final HubJpaRepository hubRepository;
	private final HubRouteJpaRepository hubRouteRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (hubRepository.count() > 0) {
			return; // 이미 시드 데이터 존재
		}
		List<Hub> hubs = createHubs();
		hubRepository.saveAll(hubs);
		List<HubRoute> routes = createRoutes(hubs);
		hubRouteRepository.saveAll(routes);
	}

	static final UUID SEOUL_HUB_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
	static final UUID GYEONGGI_HUB_ID = UUID.fromString("10000000-0000-0000-0000-000000000002");
	static final UUID JEJU_HUB_ID = UUID.fromString("10000000-0000-0000-0000-000000000003");

	static final UUID SEOUL_MANAGER_ID = UUID.fromString("20000000-0000-0000-0000-000000000001");
	static final UUID GYEONGGI_MANAGER_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");
	static final UUID JEJU_MANAGER_ID = UUID.fromString("20000000-0000-0000-0000-000000000003");

	private List<Hub> createHubs() {
		return List.of(
			Hub.builder()
				.id(SEOUL_HUB_ID)
				.name("서울특별시 센터")
				.address("서울특별시 강남구 테헤란로 212")
				.latitude(new BigDecimal("37.4981452"))
				.longitude(new BigDecimal("127.0276368"))
				.managerId(SEOUL_MANAGER_ID)
				.managerName("서울 허브 관리자")
				.build(),
			Hub.builder()
				.id(GYEONGGI_HUB_ID)
				.name("경기도 센터")
				.address("경기도 수원시 팔달구 중부대로 341")
				.latitude(new BigDecimal("37.2636"))
				.longitude(new BigDecimal("127.0286"))
				.managerId(GYEONGGI_MANAGER_ID)
				.managerName("경기 허브 관리자")
				.build(),
			Hub.builder()
				.id(JEJU_HUB_ID)
				.name("제주특별자치도 센터")
				.address("제주특별자치도 제주시 문연로 6")
				.latitude(new BigDecimal("33.4996"))
				.longitude(new BigDecimal("126.5312"))
				.managerId(JEJU_MANAGER_ID)
				.managerName("제주 허브 관리자")
				.build()
		);
	}

	private List<HubRoute> createRoutes(List<Hub> hubs) {
		List<HubRoute> routes = new ArrayList<>();

		Hub seoul = hubs.get(0);
		Hub gyeonggi = hubs.get(1);
		Hub jeju = hubs.get(2);

		// 서울 ↔ 경기
		routes.add(HubRoute.builder()
			.departureHub(seoul)
			.arrivalHub(gyeonggi)
			.distance(new BigDecimal("30.00"))
			.duration(45)
			.build());
		routes.add(HubRoute.builder()
			.departureHub(gyeonggi)
			.arrivalHub(seoul)
			.distance(new BigDecimal("30.00"))
			.duration(45)
			.build());
		// 서울 ↔ 제주
		routes.add(HubRoute.builder()
			.departureHub(seoul)
			.arrivalHub(jeju)
			.distance(new BigDecimal("465.00"))
			.duration(480)
			.build());
		routes.add(HubRoute.builder()
			.departureHub(jeju)
			.arrivalHub(seoul)
			.distance(new BigDecimal("465.00"))
			.duration(480)
			.build());
		// 경기 ↔ 제주
		routes.add(HubRoute.builder()
			.departureHub(gyeonggi)
			.arrivalHub(jeju)
			.distance(new BigDecimal("480.00"))
			.duration(500)
			.build());
		routes.add(HubRoute.builder()
			.departureHub(jeju)
			.arrivalHub(gyeonggi)
			.distance(new BigDecimal("480.00"))
			.duration(500)
			.build());

		return routes;
	}
}
