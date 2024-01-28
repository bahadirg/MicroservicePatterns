


	This project is a sample implementation of various resilience patterns for microservices based on Resilience4j.
	It also contains JUnit tests to ensure the correct behaviour of patterns. 
	
	It contains:
	
		* CircuitBreaker
		* Bulkhead
		* RateLimiter
		* Retry
		* TimeLimiter
		* Spring Cloud OpenFeign
		
	Note: This project depends on EurekaRegistryServer project which also resides in this Github account.
	      EurekaRegistryServer must be started first, before this project.
		
		
	Execution: 	mvn clean package spring-boot:run -DskipTests