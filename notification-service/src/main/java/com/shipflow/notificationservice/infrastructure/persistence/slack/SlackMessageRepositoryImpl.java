package com.shipflow.notificationservice.infrastructure.persistence.slack;

import static com.shipflow.notificationservice.domain.slack.QSlackMessage.*;

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
import com.shipflow.notificationservice.application.slack.dto.command.SearchSlackMessageCommand;
import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.repository.SlackMessageRepository;
import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlackMessageRepositoryImpl implements SlackMessageRepository {

	private final SlackMessageJpaRepository slackMessageJpaRepository;
	private final JPAQueryFactory queryFactory;

	@Override
	public SlackMessage save(SlackMessage slackMessage) {
		return slackMessageJpaRepository.save(slackMessage);
	}

	@Override
	public Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID slackId) {
		return slackMessageJpaRepository.findByIdAndDeletedAtIsNull(slackId);
	}

	@Override
	public Page<SlackMessage> search(SearchSlackMessageCommand command, Pageable pageable) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(slackMessage.deletedAt.isNull());
		builder.and(receiverSlackIdEq(command.receiverSlackId()));
		builder.and(sendStatusEq(command.sendStatus()));
		builder.and(messageTypeEq(command.messageType()));
		builder.and(createdAtGoe(command.createdAtFrom()));
		builder.and(createdAtLoe(command.createdAtTo()));

		List<SlackMessage> content = queryFactory
			.selectFrom(slackMessage)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(slackMessage.id.count())
			.from(slackMessage)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total == null ? 0L : total);
	}

	// ===== 조건 메서드 =====

	private com.querydsl.core.types.Predicate receiverSlackIdEq(String receiverSlackId) {
		return (receiverSlackId == null || receiverSlackId.isBlank())
			? null
			: slackMessage.receiverSlackId.eq(receiverSlackId);
	}

	private com.querydsl.core.types.Predicate sendStatusEq(SlackSendStatus sendStatus) {
		return sendStatus == null ? null : slackMessage.sendStatus.eq(sendStatus);
	}

	private com.querydsl.core.types.Predicate messageTypeEq(SlackMessageType messageType) {
		return messageType == null ? null : slackMessage.messageType.eq(messageType);
	}

	private com.querydsl.core.types.Predicate createdAtGoe(LocalDateTime from) {
		return from == null ? null : slackMessage.createdAt.goe(from);
	}

	private com.querydsl.core.types.Predicate createdAtLoe(LocalDateTime to) {
		return to == null ? null : slackMessage.createdAt.loe(to);
	}

}