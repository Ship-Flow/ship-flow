package com.shipflow.hubservice.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Hub Service API")
				.description("허브 및 허브 경로 관리 API")
				.version("v1"));
	}

	@Bean
	public OperationCustomizer globalHeaders() {
		return (operation, handlerMethod) -> {
			operation.addParametersItem(new Parameter()
				.in("header")
				.name("X-User-Id")
				.description("요청 사용자 ID (UUID)")
				.required(false));
			return operation;
		};
	}
}