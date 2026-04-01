package com.shipflow.common.config;

import com.shipflow.common.exception.ApiControllerAdvice;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(ApiControllerAdvice.class)
public class CommonAutoConfiguration {
}