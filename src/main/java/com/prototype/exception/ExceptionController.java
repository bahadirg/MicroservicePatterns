package com.prototype.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler({ BulkheadFullException.class })
	public ResponseEntity<String> handleBulkheadFullException() {
		return new ResponseEntity<String>("BANDWIDTH_LIMIT_EXCEEDED", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
	}
	
	@ExceptionHandler({ RequestNotPermitted.class })
	public ResponseEntity<String> handleRateLimiterException() {
		return new ResponseEntity<String>("BANDWIDTH_LIMIT_EXCEEDED", HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
	}
}
