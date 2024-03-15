package com.prototype.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.prototype.dto.Step1Dto;
import com.prototype.dto.Step2Dto;
import com.prototype.dto.Step3Dto;

@Configuration
public class KafkaConsumerConfig {
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServerUri;
	
//	@Value("${spring.embedded.kafka.brokers}")
//	private String embeddedBootstrapServerUri;

	@Bean
	public ConsumerFactory<String, Step1Dto> consumerFactoryForStep1() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUri);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		 JsonDeserializer<Step1Dto> valueDeserializer = new JsonDeserializer<>(Step1Dto.class);
		 valueDeserializer.setUseTypeMapperForKey(true);
		 valueDeserializer.addTrustedPackages("*");
								
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getClass().getName());       

		return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), valueDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Step1Dto> kafkaListenerContainerFactoryForStep1() {
		ConcurrentKafkaListenerContainerFactory<String, Step1Dto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryForStep1());

		return factory;
	}
	
	@Bean
	public ConsumerFactory<String, Step2Dto> consumerFactoryForStep2() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUri);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		 JsonDeserializer<Step2Dto> valueDeserializer = new JsonDeserializer<>(Step2Dto.class);
		 valueDeserializer.setUseTypeMapperForKey(true);
		 valueDeserializer.addTrustedPackages("*");
								
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getClass().getName());       

		return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), valueDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Step2Dto> kafkaListenerContainerFactoryForStep2() {
		ConcurrentKafkaListenerContainerFactory<String, Step2Dto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryForStep2());

		return factory;
	}
	
	
	
	
	@Bean
	public ConsumerFactory<String, Step3Dto> consumerFactoryForStep3() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUri);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		 JsonDeserializer<Step3Dto> valueDeserializer = new JsonDeserializer<>(Step3Dto.class);
		 valueDeserializer.setUseTypeMapperForKey(true);
		 valueDeserializer.addTrustedPackages("*");
								
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getClass().getName());       

		return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), valueDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Step3Dto> kafkaListenerContainerFactoryForStep3() {
		ConcurrentKafkaListenerContainerFactory<String, Step3Dto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryForStep3());

		return factory;
	}
}
