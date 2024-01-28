package com.prototype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.netflix.discovery.EurekaClient;
import com.prototype.configserver.ConfigServerValues;
import com.prototype.feignclient.PatternCallerClient;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest
@DisplayName("MicroservicePatterns Tests")
class MicroservicePatternsApplicationTests {

	@Autowired
	private EurekaClient eurekaClient;
	
	@Autowired
	private PatternCallerClient productQueryHandlerFeignClient;
	
	@Autowired
	private CircuitBreakerRegistry registry;
	
	@Autowired
	private ConfigServerValues configServerValues;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void test1_ServiceRegistryAccess() {
		
		String appName = "MY-SPRING-CLOUD-EUREKA-CLIENT";
		String retrievedAppName = eurekaClient.getApplication(appName).getName();
		assertEquals(appName.toLowerCase(), retrievedAppName.toLowerCase());
	}
	
	
	@Test
	public void test2_CircuitBreaker() {
		
		try {
			productQueryHandlerFeignClient.reset("MyCircuitBreaker");
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		ResponseEntity<String> response = productQueryHandlerFeignClient.circuitbreaker(true);
		
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals((response.getBody()), "CircuitBreaker CLOSED");
		
		// creating error to make circuitbreaker to switch to OPEN state
		for(int i = 0; i < 3; i++) {
			
			try {
				response = productQueryHandlerFeignClient.circuitbreaker(false);
			} catch (Exception e) {
				e.printStackTrace();
				
				assertTrue(e.getMessage().indexOf(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())) >= 0);
				String status = productQueryHandlerFeignClient.getCBStatus("MyCircuitBreaker").getBody();
				assertEquals(status, "CLOSED");
				
				continue;
			}
		}
		
		// viewing the OPEN & HALF-OPEN states
		for(int i = 0; i < 1000; i++) {
		
			try {
				response = productQueryHandlerFeignClient.circuitbreaker(false);
			} catch (Exception e) {
				
				String status = productQueryHandlerFeignClient.getCBStatus("MyCircuitBreaker").getBody();
				System.out.println(status);
				
				assertTrue(e.getMessage().indexOf(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())) >= 0);
				assertTrue(status.indexOf("OPEN") >= 0);
			}
			
		}	
	}
	
	@Test
	public void test3_Retry() {
		
		ResponseEntity<String> response = null;
		
		try {
			response = productQueryHandlerFeignClient.retryApi();
			assertTrue(response.getBody().indexOf("EXHAUSTED") >= 0);
		} catch (Exception e1) {
			assertTrue(e1.getMessage().indexOf("EXHAUSTED") >= 0);
			return;
		}
	}

	@Test
	public void test4_TimeLimit() {
		
		CompletableFuture<ResponseEntity<String>> response = null;
		
		try {
			 response = CompletableFuture.supplyAsync(() -> productQueryHandlerFeignClient.timeLimiterApi())
							 			 .orTimeout(6000, TimeUnit.MILLISECONDS)
							 			 .exceptionally(ex -> {
								 		
											 if(ex != null) { 
												 ex.printStackTrace();
												 assertTrue(ex instanceof TimeoutException);
											 } else {
												 fail("@TimeLimiter not timed out");
											 }
											 
											 return null;
										 });
			
			 assertTrue(response.get().getBody().indexOf("TimeLimit Exceeded") >= 0);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail("@TimeLimiter had not timed out");
		}
	}
	
	@Test
	public void test5_Bulkhead() {
		
		ResponseEntity<String> response = null;
		
		Callable<ResponseEntity<String>> bulkheadCaller = new Callable<ResponseEntity<String>>() {
			public ResponseEntity<String> call(){
				return productQueryHandlerFeignClient.bulkheadApi();
			}; 
		};
		
		try {
			FutureTask<ResponseEntity<String>> futureTask1 = new FutureTask<ResponseEntity<String>>(bulkheadCaller);
			Thread t1 = new Thread(futureTask1);
			
			FutureTask<ResponseEntity<String>> futureTask2 = new FutureTask<ResponseEntity<String>>(bulkheadCaller);
			Thread t2 = new Thread(futureTask2);
			
			FutureTask<ResponseEntity<String>> futureTask3 = new FutureTask<ResponseEntity<String>>(bulkheadCaller);
			Thread t3 = new Thread(futureTask3);
			
			t1.start();
			t2.start();
			t3.start();
			
			assertTrue(futureTask1.get().getBody().indexOf("success") >= 0);
			assertTrue(futureTask2.get().getBody().indexOf("success") >= 0);
			Throwable exception =  assertThrows(ExecutionException.class, () -> futureTask3.get());
			
			assertTrue(exception.getMessage().indexOf("BANDWIDTH_LIMIT_EXCEEDED") >= 0);			
		} catch (Exception e1) {
			e1.printStackTrace();
			fail("failed");
			return;
		}
		
	}
	
	@Test
	public void test6_RateLimiter() {
		
		ResponseEntity<String> response = null;
		
		Callable<ResponseEntity<String>> rateLimiterCaller = new Callable<ResponseEntity<String>>() {
			public ResponseEntity<String> call(){
				return productQueryHandlerFeignClient.rateLimiterApi();
			}; 
		};
		
		try {
			FutureTask<ResponseEntity<String>> futureTask1 = new FutureTask<ResponseEntity<String>>(rateLimiterCaller);
			Thread t1 = new Thread(futureTask1);
			
			FutureTask<ResponseEntity<String>> futureTask2 = new FutureTask<ResponseEntity<String>>(rateLimiterCaller);
			Thread t2 = new Thread(futureTask2);
			
			FutureTask<ResponseEntity<String>> futureTask3 = new FutureTask<ResponseEntity<String>>(rateLimiterCaller);
			Thread t3 = new Thread(futureTask3);
			
			//max 2 concurrent call allowed in 5 seconds
			t1.start();
			t2.start();
			Thread.sleep(5000);  // waiting for limit-refresh-period
			t3.start();
			
			assertTrue(futureTask1.get().getBody().indexOf("success") >= 0);
			assertTrue(futureTask2.get().getBody().indexOf("success") >= 0);
			assertTrue(futureTask3.get().getBody().indexOf("success") >= 0);
			
			Thread.sleep(5000);  // waiting for limit-refresh-period
			
			FutureTask<ResponseEntity<String>> futureTask4 = new FutureTask<ResponseEntity<String>>(rateLimiterCaller);
			Thread t4 = new Thread(futureTask4);
			
			FutureTask<ResponseEntity<String>> futureTask5 = new FutureTask<ResponseEntity<String>>(rateLimiterCaller);
			Thread t5 = new Thread(futureTask5);
			
			FutureTask<ResponseEntity<String>> futureTask6 = new FutureTask<ResponseEntity<String>>(rateLimiterCaller);
			Thread t6 = new Thread(futureTask6);
			
			//max 2 concurrent call allowed in 5 seconds, 3rd will get error
			t4.start();
			t5.start();
			t6.start();
			
			assertTrue(futureTask4.get().getBody().indexOf("success") >= 0);
			assertTrue(futureTask5.get().getBody().indexOf("success") >= 0);
			
			try {
				futureTask6.get();
				fail("RateLimiter did not work!");
			} catch (Throwable e) {
				assertTrue(true);
			}
			
		} catch (Throwable e1) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	public void test7_ConfigServerAccess() {
		assertEquals(configServerValues.getDummyKey1(), "dummyValue1");
	}
}
	