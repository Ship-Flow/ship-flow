package com.shipflow.productservice.infrastructure.messaging.config;

import com.shipflow.config.message.RabbitMqConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRabbitConfig {

	private static final String ROUTING_ORDER_CREATION_STARTED = "order.creation.started";

	public static final String QUEUE_PRODUCT_ORDER_CREATION_STARTED = "product.order.creation.started";
	public static final String QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ = QUEUE_PRODUCT_ORDER_CREATION_STARTED + ".dlq";

	@Bean
	public Queue queueProductOrderCreationStarted() {
		return RabbitMqConfig.durableQueue(QUEUE_PRODUCT_ORDER_CREATION_STARTED);
	}

	@Bean
	public Queue queueProductOrderCreationStartedDlq() {
		return RabbitMqConfig.dlqQueue(QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ);
	}

	@Bean
	public Binding bindProductOrderCreationStarted(TopicExchange sagaExchange) {
		return BindingBuilder.bind(queueProductOrderCreationStarted())
			.to(sagaExchange)
			.with(ROUTING_ORDER_CREATION_STARTED);
	}

	@Bean
	public Binding bindProductOrderCreationStartedDlq(DirectExchange sagaDlx) {
		return BindingBuilder.bind(queueProductOrderCreationStartedDlq())
			.to(sagaDlx)
			.with(QUEUE_PRODUCT_ORDER_CREATION_STARTED_DLQ);
	}
}
