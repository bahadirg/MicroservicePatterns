package com.prototype.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.prototype.dto.Step1Dto;
import com.prototype.dto.Step2Dto;
import com.prototype.dto.Step3Dto;

@Configuration
public class KafkaProducerConfig {
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServerUri;
	
	
	@Bean("producerFactoryForTopic1")
	public ProducerFactory<String, Step1Dto> producerFactoryForTopic1() {
		
		JsonSerializer<Step1Dto> valueSerializer = new JsonSerializer<Step1Dto>();
		
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUri);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getClass().getName());           

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean("kafkaTemplate1")
	@DependsOn("producerFactoryForTopic1")  
	public KafkaTemplate<String, Step1Dto> kafkaTemplate1() {                                                        
		return new KafkaTemplate<String, Step1Dto>(producerFactoryForTopic1());
	}
	
	@Bean("producerFactoryForTopic2")
	public ProducerFactory<String, Step2Dto> producerFactoryForTopic2() {
		
		JsonSerializer<Step2Dto> valueSerializer = new JsonSerializer<Step2Dto>();
		
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUri);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getClass().getName());           

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean("kafkaTemplate2")
	@DependsOn("producerFactoryForTopic2")  
	public KafkaTemplate<String, Step2Dto> kafkaTemplate2() {                                                        
		return new KafkaTemplate<String, Step2Dto>(producerFactoryForTopic2());
	}
	
	@Bean("producerFactoryForTopic3")
	public ProducerFactory<String, Step3Dto> producerFactoryForTopic3() {
		
		JsonSerializer<Step3Dto> valueSerializer = new JsonSerializer<Step3Dto>();
		
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUri);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getClass().getName());           

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean("kafkaTemplate3")
	@DependsOn("producerFactoryForTopic3")    
	public KafkaTemplate<String, Step3Dto> kafkaTemplate3() {                                                        
		return new KafkaTemplate<String, Step3Dto>(producerFactoryForTopic3());
	}
}
