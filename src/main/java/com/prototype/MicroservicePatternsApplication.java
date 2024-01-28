package com.prototype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroservicePatternsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicePatternsApplication.class, args);
	}

}
