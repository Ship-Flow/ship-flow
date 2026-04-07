package com.shipflow.config.message;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	// ── Exchange 이름 ──────────────────────────────
	public static final String SAGA_EXCHANGE = "saga.events";      // Topic Exchange 방식
	public static final String SAGA_DLX = "saga.events.dlx";  // Direct Exchange 방식 -> 메시지 처리 실패 시 사용

	// ── 공통 Queue 생성 헬퍼 (각 서비스에서 호출) ────
	public static Queue durableQueue(String name) {
		return QueueBuilder.durable(name)
			.withArgument("x-dead-letter-exchange", SAGA_DLX)
			.withArgument("x-dead-letter-routing-key", name + ".dlq")
			.build();
	}

	public static Queue dlqQueue(String name) {
		return QueueBuilder.durable(name).build();
	}

	// ── Exchange Bean ──────────────────────────────
	@Bean
	public TopicExchange sagaExchange() {
		return new TopicExchange(SAGA_EXCHANGE, true, false);
	}

	@Bean
	public DirectExchange sagaDlx() {
		return new DirectExchange(SAGA_DLX, true, false);
	}

	// ── RabbitTemplate (Jackson 직렬화 + Zipkin 트레이싱) ───────────
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory cf, ObjectMapper objectMapper,
			ObservationRegistry observationRegistry) {
		RabbitTemplate template = new RabbitTemplate(cf);
		template.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
		template.setObservationEnabled(true);
		return template;
	}

	@Bean
	public MessageConverter messageConverter(ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}

	// ── ListenerContainerFactory (소비 측 Zipkin 트레이싱) ───────────
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf,
			MessageConverter messageConverter, ObservationRegistry observationRegistry) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(cf);
		factory.setMessageConverter(messageConverter);
		factory.setObservationEnabled(true);
		return factory;
	}
}
