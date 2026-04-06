package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shipflow.shipmentservice.application.dto.query.ShipmentManagerSearchQuery;
import com.shipflow.shipmentservice.domain.QShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ShipmentManagerCustomRepository {

	private final JPAQueryFactory queryFactory;

	public List<ShipmentManager> search(ShipmentManagerSearchQuery query, Pageable pageable) {
		QShipmentManager sm = QShipmentManager.shipmentManager;

		BooleanBuilder condition = new BooleanBuilder();
		condition.and(sm.deletedAt.isNull());

		log.info("query: {}", query);

		if (query.getType() != null) {
			condition.and(sm.type.eq(query.getType()));
		}
		if (query.getHubId() != null) {
			condition.and(sm.hubId.eq(query.getHubId()));
		}

		return queryFactory.selectFrom(sm)
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}

