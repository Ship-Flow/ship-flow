package com.shipflow.shipmentservice.infrastructure.messaging.config;

import com.shipflow.config.message.RabbitMqConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShipmentRabbitConfig {

	// Routing Keys
	private static final String ROUTING_PRODUCT_STOCK_DECREASED = "product.stock.decreased";
	private static final String ROUTING_ORDER_CREATED = "order.created";
	private static final String ROUTING_ORDER_CANCELED = "order.canceled";

	// Queue Names
	public static final String QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED = "shipment.product.stock.decreased";
	public static final String QUEUE_SHIPMENT_ORDER_CREATED = "shipment.order.created";
	public static final String QUEUE_SHIPMENT_ORDER_CANCELED = "shipment.order.canceled";

	// DLQ Names
	private static final String QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED_DLQ =
		QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED + ".dlq";
	private static final String QUEUE_SHIPMENT_ORDER_CREATED_DLQ = QUEUE_SHIPMENT_ORDER_CREATED + ".dlq";
	private static final String QUEUE_SHIPMENT_ORDER_CANCELED_DLQ = QUEUE_SHIPMENT_ORDER_CANCELED + ".dlq";

	// ---- product.stock.decreased ----

	@Bean
	public Queue queueShipmentProductStockDecreased() {
		return RabbitMqConfig.durableQueue(QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED);
	}

	@Bean
	public Queue queueShipmentProductStockDecreasedDlq() {
		return RabbitMqConfig.dlqQueue(QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED_DLQ);
	}

	@Bean
	public Binding bindShipmentProductStockDecreased(TopicExchange sagaExchange) {
		return BindingBuilder.bind(queueShipmentProductStockDecreased())
			.to(sagaExchange)
			.with(ROUTING_PRODUCT_STOCK_DECREASED);
	}

	@Bean
	public Binding bindShipmentProductStockDecreasedDlq(DirectExchange sagaDlx) {
		return BindingBuilder.bind(queueShipmentProductStockDecreasedDlq())
			.to(sagaDlx)
			.with(QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED_DLQ);
	}

	// ---- order.created ----

	@Bean
	public Queue queueShipmentOrderCreated() {
		return RabbitMqConfig.durableQueue(QUEUE_SHIPMENT_ORDER_CREATED);
	}

	@Bean
	public Queue queueShipmentOrderCreatedDlq() {
		return RabbitMqConfig.dlqQueue(QUEUE_SHIPMENT_ORDER_CREATED_DLQ);
	}

	@Bean
	public Binding bindShipmentOrderCreated(TopicExchange sagaExchange) {
		return BindingBuilder.bind(queueShipmentOrderCreated())
			.to(sagaExchange)
			.with(ROUTING_ORDER_CREATED);
	}

	@Bean
	public Binding bindShipmentOrderCreatedDlq(DirectExchange sagaDlx) {
		return BindingBuilder.bind(queueShipmentOrderCreatedDlq())
			.to(sagaDlx)
			.with(QUEUE_SHIPMENT_ORDER_CREATED_DLQ);
	}

	// ---- order.canceled ----

	@Bean
	public Queue queueShipmentOrderCanceled() {
		return RabbitMqConfig.durableQueue(QUEUE_SHIPMENT_ORDER_CANCELED);
	}

	@Bean
	public Queue queueShipmentOrderCanceledDlq() {
		return RabbitMqConfig.dlqQueue(QUEUE_SHIPMENT_ORDER_CANCELED_DLQ);
	}

	@Bean
	public Binding bindShipmentOrderCanceled(TopicExchange sagaExchange) {
		return BindingBuilder.bind(queueShipmentOrderCanceled())
			.to(sagaExchange)
			.with(ROUTING_ORDER_CANCELED);
	}

	@Bean
	public Binding bindShipmentOrderCanceledDlq(DirectExchange sagaDlx) {
		return BindingBuilder.bind(queueShipmentOrderCanceledDlq())
			.to(sagaDlx)
			.with(QUEUE_SHIPMENT_ORDER_CANCELED_DLQ);
	}
}
