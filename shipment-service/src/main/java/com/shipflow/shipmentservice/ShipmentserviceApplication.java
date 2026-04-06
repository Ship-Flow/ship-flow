package com.shipflow.shipmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ShipmentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipmentserviceApplication.class, args);
	}

}
