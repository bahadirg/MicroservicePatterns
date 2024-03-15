package com.prototype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.netflix.discovery.EurekaClient;
import com.prototype.configserver.ConfigServerValues;
import com.prototype.dto.Step1Dto;
import com.prototype.feignclient.PatternCallerClient;
import com.prototype.repository.Step1Repository;
import com.prototype.repository.Step2Repository;
import com.prototype.repository.Step3Repository;
import com.prototype.saga.KafkaConsumerService;
import com.prototype.saga.KafkaProducerService;

@SpringBootTest
@DirtiesContext
@DisplayName("MicroservicePatterns Tests")
class MicroservicePatternsApplicationTests {

	@Autowired
	private EurekaClient eurekaClient;
	
	@Autowired
	private PatternCallerClient productQueryHandlerFeignClient;
	
	@Autowired
	private ConfigServerValues configServerValues;
	
	@SpyBean
//	@Autowired
	KafkaProducerService kafkaProducerService;
	
	@InjectMocks
	KafkaConsumerService kafkaConsumerService;
	
	@SpyBean
	Step1Repository step1Repository;
	
	@SpyBean
	Step2Repository step2Repository;
	
	@SpyBean
	Step3Repository step3Repository;
	
	@ClassRule
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
	
	
	// To avoid port clashes, Testcontainers allocates a port number dynamically, overriding config with it
	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
	
	
	@BeforeAll
    public static void startContainer() {
		kafkaContainer.start();
		
		
        String bootstrapServers = kafkaContainer.getBootstrapServers();

        Properties adminClientProperties = new Properties();
        adminClientProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (AdminClient adminClient = AdminClient.create(adminClientProperties)) {

            int numPartitions = 1;

            // Create a NewTopic object
            NewTopic newTopic = new NewTopic("topic1", numPartitions, (short) 1);

            // Create the topic
            Set<NewTopic> topicSet = new HashSet<NewTopic>();
            topicSet.add(newTopic);
            newTopic = new NewTopic("topic2", numPartitions, (short) 1);
            topicSet.add(newTopic);
            newTopic = new NewTopic("topic3", numPartitions, (short) 1);
            topicSet.add(newTopic);
            newTopic = new NewTopic("rollbackTopic1", numPartitions, (short) 1);
            topicSet.add(newTopic);
            newTopic = new NewTopic("rollbackTopic2", numPartitions, (short) 1);
            topicSet.add(newTopic);
            adminClient.createTopics(topicSet).all().get();

            // Verify that the topic has been created
            Set<String> existingTopics = adminClient.listTopics().names().get();
            System.out.println("existingTopics: " + existingTopics);
            
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	
	@Test
	public void test8_Saga_success_scenario() {
		
		reset(step1Repository);
		reset(step2Repository);
		reset(step3Repository);
		reset(kafkaProducerService);
		
		Step1Dto step1Dto = new Step1Dto();
		step1Dto.setStId(22);
		step1Dto.setAge(35);
		step1Dto.setName("Mehmet");
		step1Dto.setRollbackAtStep(0);
		kafkaProducerService.sendMessageOfTypeStep1("topic1", step1Dto);
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		verify(step1Repository, times(1)).save(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep2(any(), any());
		assertTrue(step1Repository.findById(22).isPresent());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		verify(step2Repository, times(1)).save(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep3(any(), any());
		
		assertTrue(step2Repository.findById(22).isPresent());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		verify(step3Repository, times(1)).save(any());
		
		assertTrue(step3Repository.findById(22).isPresent());
		
		
		step1Repository.deleteAll();
		step2Repository.deleteAll();
		step3Repository.deleteAll();
	}
	
	
	@Test
	public void test9_Saga_step2_rollback_scenario() {
		
		reset(step1Repository);
		reset(step2Repository);
		reset(step3Repository);
		reset(kafkaProducerService);
		
		Step1Dto step1Dto = new Step1Dto();
		step1Dto.setStId(33);
		step1Dto.setAge(36);
		step1Dto.setName("Ahmet");
		step1Dto.setRollbackAtStep(2);
		kafkaProducerService.sendMessageOfTypeStep1("topic1", step1Dto);
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		verify(step1Repository, times(1)).save(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep2(any(), any());
		
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		verify(step2Repository, times(0)).save(any());
		verify(kafkaProducerService).sendMessageOfTypeStep1(eq("rollbackTopic1"), any());
		
		assertFalse(step2Repository.findById(33).isPresent());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		verify(step1Repository, times(1)).deleteById(any());
		assertFalse(step1Repository.findById(33).isPresent());
		
		
		step1Repository.deleteAll();
		step2Repository.deleteAll();
		step3Repository.deleteAll();
	}
	
	@Test
	public void test10_Saga_step3_rollback_scenario() {
		
		reset(step1Repository);
		reset(step2Repository);
		reset(step3Repository);
		reset(kafkaProducerService);
		
		Step1Dto step1Dto = new Step1Dto();
		step1Dto.setStId(44);
		step1Dto.setAge(37);
		step1Dto.setName("Fikret");
		step1Dto.setRollbackAtStep(3);
		kafkaProducerService.sendMessageOfTypeStep1("topic1", step1Dto);
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		verify(step1Repository, times(1)).save(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep2(eq("topic2"), any());
		
		//assertTrue(step1Repository.findAll().iterator().hasNext());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		verify(step2Repository, times(1)).save(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep3(eq("topic3"), any());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		verify(step3Repository, times(0)).save(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep2(eq("rollbackTopic2"), any());
		
		assertFalse(step3Repository.findById(44).isPresent());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		verify(step2Repository, times(1)).deleteById(any());
		verify(kafkaProducerService, times(1)).sendMessageOfTypeStep1(eq("rollbackTopic1"), any());
		
		assertFalse(step2Repository.findById(44).isPresent());
		
		try {
			Thread.sleep(2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		verify(step1Repository, times(1)).deleteById(any());
		
		assertFalse(step1Repository.findById(44).isPresent());
		
		
		step1Repository.deleteAll();
		step2Repository.deleteAll();
		step3Repository.deleteAll();
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
			Thread.sleep(1000L);
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
			Thread.sleep(1000);
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
	