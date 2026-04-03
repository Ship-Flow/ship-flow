package com.shipflow.notificationservice.domain.slack.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.SlackMessage;

public interface SlackMessageRepository {

	SlackMessage save(SlackMessage slackMessage);

	Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID slackId);

	List<SlackMessage> findAllByDeletedAtIsNull();
}