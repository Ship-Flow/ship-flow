package com.shipflow.common.config;

import com.shipflow.common.exception.ApiControllerAdvice;
import com.shipflow.common.messaging.publisher.RabbitEventPublisher;
import com.shipflow.config.message.JacksonConfig;
import com.shipflow.config.message.RabbitMqConfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        ApiControllerAdvice.class,
        JacksonConfig.class,
        RabbitMqConfig.class,
        RabbitEventPublisher.class
})
public class CommonAutoConfiguration {
}
