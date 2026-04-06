package com.shipflow.hubservice.infrastructure.initializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	private List<Hub> createHubs() {
		UUID managerId = UUID.fromString("00000000-0000-0000-0000-000000000001");

		return List.of(
			Hub.builder()
				.name("서울특별시 센터")
				.address("서울특별시 강남구 테헤란로 212")
				.latitude(new BigDecimal("37.4981452"))
				.longitude(new BigDecimal("127.0276368"))
				.managerId(managerId)
				.managerName("서울 허브 관리자")
				.build(),
			Hub.builder()
				.name("경기도 센터")
				.address("경기도 수원시 팔달구 중부대로 341")
				.latitude(new BigDecimal("37.2636"))
				.longitude(new BigDecimal("127.0286"))
				.managerId(managerId)
				.managerName("경기 허브 관리자")
				.build(),
			Hub.builder()
				.name("인천광역시 센터")
				.address("인천광역시 남동구 인주대로 590")
				.latitude(new BigDecimal("37.4563"))
				.longitude(new BigDecimal("126.7052"))
				.managerId(managerId)
				.managerName("인천 허브 관리자")
				.build(),
			Hub.builder()
				.name("강원특별자치도 센터")
				.address("강원특별자치도 춘천시 중앙로 1")
				.latitude(new BigDecimal("37.8813"))
				.longitude(new BigDecimal("127.7298"))
				.managerId(managerId)
				.managerName("강원 허브 관리자")
				.build(),
			Hub.builder()
				.name("충청북도 센터")
				.address("충청북도 청주시 상당구 상당로 82")
				.latitude(new BigDecimal("36.6424"))
				.longitude(new BigDecimal("127.4890"))
				.managerId(managerId)
				.managerName("충북 허브 관리자")
				.build(),
			Hub.builder()
				.name("충청남도 센터")
				.address("충청남도 홍성군 홍북읍 충남대로 21")
				.latitude(new BigDecimal("36.6588"))
				.longitude(new BigDecimal("126.6728"))
				.managerId(managerId)
				.managerName("충남 허브 관리자")
				.build(),
			Hub.builder()
				.name("대전광역시 센터")
				.address("대전광역시 서구 둔산대로 100")
				.latitude(new BigDecimal("36.3504"))
				.longitude(new BigDecimal("127.3845"))
				.managerId(managerId)
				.managerName("대전 허브 관리자")
				.build(),
			Hub.builder()
				.name("세종특별자치시 센터")
				.address("세종특별자치시 한누리대로 2130")
				.latitude(new BigDecimal("36.4800"))
				.longitude(new BigDecimal("127.2890"))
				.managerId(managerId)
				.managerName("세종 허브 관리자")
				.build(),
			Hub.builder()
				.name("전북특별자치도 센터")
				.address("전북특별자치도 전주시 완산구 효자로 225")
				.latitude(new BigDecimal("35.8242"))
				.longitude(new BigDecimal("127.1480"))
				.managerId(managerId)
				.managerName("전북 허브 관리자")
				.build(),
			Hub.builder()
				.name("전라남도 센터")
				.address("전라남도 무안군 삼향읍 오룡길 1")
				.latitude(new BigDecimal("34.8679"))
				.longitude(new BigDecimal("126.9910"))
				.managerId(managerId)
				.managerName("전남 허브 관리자")
				.build(),
			Hub.builder()
				.name("광주광역시 센터")
				.address("광주광역시 서구 내방로 111")
				.latitude(new BigDecimal("35.1595"))
				.longitude(new BigDecimal("126.8526"))
				.managerId(managerId)
				.managerName("광주 허브 관리자")
				.build(),
			Hub.builder()
				.name("경상북도 센터")
				.address("경상북도 안동시 풍천면 도청대로 455")
				.latitude(new BigDecimal("36.5760"))
				.longitude(new BigDecimal("128.5055"))
				.managerId(managerId)
				.managerName("경북 허브 관리자")
				.build(),
			Hub.builder()
				.name("경상남도 센터")
				.address("경상남도 창원시 의창구 중앙대로 300")
				.latitude(new BigDecimal("35.4606"))
				.longitude(new BigDecimal("128.2132"))
				.managerId(managerId)
				.managerName("경남 허브 관리자")
				.build(),
			Hub.builder()
				.name("대구광역시 센터")
				.address("대구광역시 북구 옥산로 1")
				.latitude(new BigDecimal("35.8714"))
				.longitude(new BigDecimal("128.6014"))
				.managerId(managerId)
				.managerName("대구 허브 관리자")
				.build(),
			Hub.builder()
				.name("울산광역시 센터")
				.address("울산광역시 남구 중앙로 201")
				.latitude(new BigDecimal("35.5384"))
				.longitude(new BigDecimal("129.3114"))
				.managerId(managerId)
				.managerName("울산 허브 관리자")
				.build(),
			Hub.builder()
				.name("부산광역시 센터")
				.address("부산광역시 연제구 중앙대로 1001")
				.latitude(new BigDecimal("35.1796"))
				.longitude(new BigDecimal("129.0756"))
				.managerId(managerId)
				.managerName("부산 허브 관리자")
				.build(),
			Hub.builder()
				.name("제주특별자치도 센터")
				.address("제주특별자치도 제주시 문연로 6")
				.latitude(new BigDecimal("33.4996"))
				.longitude(new BigDecimal("126.5312"))
				.managerId(managerId)
				.managerName("제주 허브 관리자")
				.build()
		);
	}

	private List<HubRoute> createRoutes(List<Hub> hubs) {
		List<HubRoute> routes = new ArrayList<>();

		Hub seoul = hubs.get(0);
		Hub gyeonggi = hubs.get(1);
		Hub incheon = hubs.get(2);

		routes.add(HubRoute.builder()
			.departureHub(seoul)
			.arrivalHub(gyeonggi)
			.distance(new BigDecimal("30.00"))
			.duration(45)
			.build());
		routes.add(HubRoute.builder()
			.departureHub(seoul)
			.arrivalHub(incheon)
			.distance(new BigDecimal("40.00"))
			.duration(60)
			.build());
		routes.add(HubRoute.builder()
			.departureHub(gyeonggi)
			.arrivalHub(incheon)
			.distance(new BigDecimal("25.00"))
			.duration(35)
			.build());

		Set<String> added = new HashSet<>(Set.of("0-1", "0-2", "1-2"));
		for (int i = 0; i < hubs.size(); i++) {
			for (int j = 0; j < hubs.size(); j++) {
				if (i != j && !added.contains(i + "-" + j)) {
					routes.add(HubRoute.builder()
						.departureHub(hubs.get(i))
						.arrivalHub(hubs.get(j))
						.distance(BigDecimal.ONE)
						.duration(1)
						.build());
				}
			}
		}

		return routes;
	}
}
