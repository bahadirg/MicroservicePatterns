package com.prototype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories(basePackages = "com.prototype.repository", entityManagerFactoryRef = "entityManagerFactory")   //jpaSharedEM_entityManagerFactory
@EntityScan(basePackages = "com.prototype.entity")
public class MicroservicePatternsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MicroservicePatternsApplication.class, args);
		;
	}

}
