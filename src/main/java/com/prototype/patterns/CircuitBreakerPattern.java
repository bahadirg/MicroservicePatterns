package com.prototype.patterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Controller
public class CircuitBreakerPattern {

	@Autowired
	private CircuitBreakerRegistry registry;
	
	
	
	@CircuitBreaker(name = "MyCircuitBreaker", fallbackMethod = "fallback")
	@RequestMapping(value = "/api/circuitbreaker", method = RequestMethod.GET)
	public ResponseEntity<String> circuitbreaker(@RequestParam boolean isSuccess) throws Exception {
		
		HttpStatusCode status = isSuccess ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		
		if(isSuccess) {
			return new ResponseEntity<>("CircuitBreaker CLOSED", HttpStatus.OK);
		}
		
		throw new Exception("Dummy Error");
	}
	
	public ResponseEntity<String> fallback(boolean isSuccess, CallNotPermittedException e) {
		return new ResponseEntity<>("CircuitBreaker (HALF) OPEN", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	
	///////////////////
	
	
	@GetMapping("/api/circuitbreaker/status")
	public ResponseEntity<String> getCBStatus(@RequestParam String circuitBreakerName) {
		
		io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = registry.find("MyCircuitBreaker").get();    // Retrieve or create a CircuitBreaker by name
		io.github.resilience4j.circuitbreaker.CircuitBreaker.State currentState = circuitBreaker.getState();              // Get the status of the CircuitBreaker
		return new ResponseEntity<String>(currentState.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/circuitbreaker/reset", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> reset(@RequestBody String circuitBreakerName) {
		
		try {
			io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = registry.find("MyCircuitBreaker").get();
			circuitBreaker.reset();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}
		
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}
