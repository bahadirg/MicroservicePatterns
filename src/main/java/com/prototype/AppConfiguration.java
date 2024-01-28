package com.prototype;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

//	@Bean
//    public ErrorDecoder errorDecoder() {  // tells Feign client how to handle HTTP 500 Error
//        return new CustomErrorDecoder();  // do not throw exception for HTTP 500 errors
//    }
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
