package com.shipflow.common.config;

import com.shipflow.common.exception.ApiControllerAdvice;
import com.shipflow.config.message.JacksonConfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
	ApiControllerAdvice.class,
	JacksonConfig.class,
})
public class CommonAutoConfiguration {
}
