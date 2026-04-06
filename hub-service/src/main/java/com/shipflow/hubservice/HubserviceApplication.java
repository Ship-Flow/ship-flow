package com.shipflow.hubservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class HubserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubserviceApplication.class, args);
	}

}
