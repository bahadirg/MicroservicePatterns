package com.prototype.patterns;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
public class TimeLimiterPattern {
	
	@Autowired
	RestTemplate restTemplate;

	
	@GetMapping("/api/time-limiter")
	@Bulkhead(name = "mybulkhead", type = Type.THREADPOOL)
	@TimeLimiter(name = "timeLimiterApi", fallbackMethod = "fallbackAfterTimeLimit")
	public CompletionStage<ResponseEntity<String>> timeLimiterApi() {
		
		String asynchResponse = "failed";
		
		try {
			asynchResponse = CompletableFuture.supplyAsync(TimeLimiterPattern::callApiWithDelay).get();
			return CompletableFuture.completedFuture(new ResponseEntity<>(asynchResponse, HttpStatus.OK));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return CompletableFuture.completedFuture(new ResponseEntity<>("failure", HttpStatus.OK));
	}
	
	public static String callApiWithDelay() {
		
		String result = "success";
//		String result = restTemplate.getForObject("/api/external", String.class);
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException ignore) {
			
		}
		
		return result;
	}
	
	public CompletionStage<ResponseEntity<String>> fallbackAfterTimeLimit(Throwable throwable) {
		return CompletableFuture.completedFuture(new ResponseEntity<>("TimeLimit Exceeded", HttpStatus.OK));
	}
}
