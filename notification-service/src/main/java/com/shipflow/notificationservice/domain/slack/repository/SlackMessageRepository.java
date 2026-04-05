package com.shipflow.notificationservice.domain.slack.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shipflow.notificationservice.domain.slack.SlackMessage;

public interface SlackMessageRepository {

	SlackMessage save(SlackMessage slackMessage);

	Optional<SlackMessage> findByIdAndDeletedAtIsNull(UUID slackId);

	Page<SlackMessage> findAllByDeletedAtIsNull(Pageable pageable);
}