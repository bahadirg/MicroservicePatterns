package com.prototype.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@FeignClient(name = "my-spring-cloud-eureka-client")
public interface PatternCallerClient {

	@RequestMapping(value = "/api/circuitbreaker", method = RequestMethod.GET)
	public ResponseEntity<String> circuitbreaker(@RequestParam boolean isSuccess);
	
	@GetMapping("/api/retry")
	public ResponseEntity<String> retryApi() throws Exception;
	
	@GetMapping("/api/time-limiter")
	public ResponseEntity<String> timeLimiterApi();
	
	@GetMapping("/api/bulkhead")
	public ResponseEntity<String> bulkheadApi();
	
	@GetMapping("/api/rate-limiter")
	public ResponseEntity<String> rateLimiterApi();
	
	////////////////
	
	@RequestMapping(value = "/api/circuitbreaker/status", method = RequestMethod.GET)
	public ResponseEntity<String> getCBStatus(@RequestParam String circuitBreakerName);
	
	@RequestMapping(value = "/api/circuitbreaker/reset", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> reset(@RequestBody String circuitBreakerName);
}
