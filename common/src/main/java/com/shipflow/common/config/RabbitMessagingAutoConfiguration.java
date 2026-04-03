package com.shipflow.common.config;

import com.shipflow.common.messaging.publisher.RabbitEventPublisher;
import com.shipflow.config.message.RabbitMqConfig;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnBean(ConnectionFactory.class)
@Import({RabbitMqConfig.class, RabbitEventPublisher.class})
public class RabbitMessagingAutoConfiguration {
}
