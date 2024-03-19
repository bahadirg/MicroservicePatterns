package com.prototype.patterns;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class RetryPattern {

	@GetMapping("/api/retry")
	@Retry(name = "retryApi", fallbackMethod = "fallbackAfterRetry" )
	public ResponseEntity<String> retryApi() throws Exception {
		System.out.println("Retried...");
		throw new Exception("Dummy Error");
	}
	
	public ResponseEntity<String> fallbackAfterRetry(Exception e) {
		return new ResponseEntity<>("Retry EXHAUSTED", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
