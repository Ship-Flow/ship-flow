package com.shipflow.notificationservice.infrastructure.persistence.ai;

import static com.shipflow.notificationservice.domain.ai.QAiLog.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shipflow.notificationservice.application.ai.dto.command.SearchAiLogCommand;
import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.repository.AiLogRepository;
import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiLogRepositoryImpl implements AiLogRepository {

	private final AiLogJpaRepository aiLogJpaRepository;
	private final JPAQueryFactory queryFactory;

	@Override
	public AiLog save(AiLog aiLog) {
		return aiLogJpaRepository.save(aiLog);
	}

	@Override
	public Optional<AiLog> findByIdAndDeletedAtIsNull(UUID aiId) {
		return aiLogJpaRepository.findByIdAndDeletedAtIsNull(aiId);
	}

	@Override
	public Page<AiLog> search(SearchAiLogCommand command, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(aiLog.deletedAt.isNull());
		builder.and(shipmentManagerIdEq(command.shipmentManagerId()));
		builder.and(requestTypeEq(command.requestType()));
		builder.and(requestStatusEq(command.requestStatus()));
		builder.and(workDateEq(command.workDate()));
		builder.and(createdAtGoe(command.createdAtFrom()));
		builder.and(createdAtLoe(command.createdAtTo()));

		List<AiLog> content = queryFactory
			.selectFrom(aiLog)
			.where(builder)
			.orderBy(aiLog.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(aiLog.id.count())
			.from(aiLog)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total == null ? 0L : total);
	}

	private com.querydsl.core.types.Predicate shipmentManagerIdEq(UUID shipmentManagerId) {
		return shipmentManagerId == null ? null : aiLog.shipmentManagerId.eq(shipmentManagerId);
	}

	private com.querydsl.core.types.Predicate requestTypeEq(AiRequestType requestType) {
		return requestType == null ? null : aiLog.requestType.eq(requestType);
	}

	private com.querydsl.core.types.Predicate requestStatusEq(AiRequestStatus requestStatus) {
		return requestStatus == null ? null : aiLog.requestStatus.eq(requestStatus);
	}

	private com.querydsl.core.types.Predicate workDateEq(LocalDate workDate) {
		return workDate == null ? null : aiLog.workDate.eq(workDate);
	}

	private com.querydsl.core.types.Predicate createdAtGoe(LocalDateTime from) {
		return from == null ? null : aiLog.createdAt.goe(from);
	}

	private com.querydsl.core.types.Predicate createdAtLoe(LocalDateTime to) {
		return to == null ? null : aiLog.createdAt.loe(to);
	}

}

