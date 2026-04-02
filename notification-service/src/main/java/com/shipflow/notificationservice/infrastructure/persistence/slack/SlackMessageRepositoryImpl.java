package com.shipflow.notificationservice.infrastructure.persistence.slack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackMessageRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlackMessageRepositoryImpl implements SlackMessageRepository {

	private final SlackMessageJpaRepository slackMessageJpaRepository;

	@Override
	public SlackMessage save(SlackMessage slackMessage) {
		return slackMessageJpaRepository.save(slackMessage);
	}

	@Override
	public Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID slackId) {
		return slackMessageJpaRepository.findByIdAndDeletedAtIsNull(slackId);
	}

	@Override
	public List<SlackMessage> findAllByDeletedAtIsNull() {
		return slackMessageJpaRepository.findAllByDeletedAtIsNull();
	}
}