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

	private static final String ROUTING_PRODUCT_STOCK_DECREASED = "product.stock.decreased";

	public static final String QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED = "shipment.product.stock.decreased";
	public static final String QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED_DLQ =
		QUEUE_SHIPMENT_PRODUCT_STOCK_DECREASED + ".dlq";

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
}
