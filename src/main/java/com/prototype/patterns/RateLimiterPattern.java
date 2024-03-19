package com.prototype.patterns;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class RateLimiterPattern {
	
	@GetMapping("/api/rate-limiter")
	@RateLimiter(name="rateLimiterApi")
	public ResponseEntity<String> rateLimiterApi() {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ignore) {
			
		}
		
		return new ResponseEntity<>("success", HttpStatus.OK);
	}
}
