


	This project is a sample implementation of various resilience patterns for microservices based on Resilience4j.
	It also contains JUnit tests to ensure the correct behaviour of patterns. 
	
	It contains:
	
		* CircuitBreaker
		* Bulkhead
		* RateLimiter
		* Retry
		* TimeLimiter
		* Spring Cloud OpenFeign
		* Saga         (Using testcontainers for Kafka & H2)
		
	Prerequisites:
	
		- This project depends on EurekaRegistryServer project which also resides in the same Github account.
	      EurekaRegistryServer must be started first, before this project.
	      
	      
	    - Docker must be installed & running for testcontainers to install mock Kafka server  
	      
		
		
	Execution: 	mvn clean package spring-boot:run -DskipTests