package com.shipflow.companyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CompanyserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyserviceApplication.class, args);
	}

}
