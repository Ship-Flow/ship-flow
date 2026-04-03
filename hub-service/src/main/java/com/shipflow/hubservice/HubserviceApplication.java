package com.shipflow.hubservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HubserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubserviceApplication.class, args);
	}

}
