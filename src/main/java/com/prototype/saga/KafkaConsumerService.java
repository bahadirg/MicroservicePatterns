package com.prototype.saga;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.prototype.dto.Step1Dto;
import com.prototype.dto.Step2Dto;
import com.prototype.dto.Step3Dto;
import com.prototype.entity.Step1Entity;
import com.prototype.entity.Step2Entity;
import com.prototype.entity.Step3Entity;
import com.prototype.mapper.Step1Mapper;
import com.prototype.mapper.Step1MapperImpl;
import com.prototype.mapper.Step2Mapper;
import com.prototype.mapper.Step2MapperImpl;
import com.prototype.mapper.Step3Mapper;
import com.prototype.mapper.Step3MapperImpl;
import com.prototype.repository.Step1Repository;
import com.prototype.repository.Step2Repository;
import com.prototype.repository.Step3Repository;
import com.prototype.util.ValueGenerator;

@Service
public class KafkaConsumerService {
	
	@Autowired
	Step1Repository step1Repository;
	
	@Autowired
	Step2Repository step2Repository;
	
	@Autowired
	Step3Repository step3Repository;
	
	Step1Mapper step1Mapper = new Step1MapperImpl();
	
	Step2Mapper step2Mapper = new Step2MapperImpl();
	
	Step3Mapper step3Mapper = new Step3MapperImpl();
	
	@Autowired
	KafkaProducerService kafkaProducerService;
	

	
	@KafkaListener(topics = "topic1", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactoryForStep1")
	public void listenTopic1(@Payload Step1Dto message) {
				
		Step1Entity step1Entity = step1Mapper.toEntity(message);	
		step1Entity = step1Repository.save(step1Entity);
	
		Step2Dto step2Dto = new Step2Dto(step1Entity.getStId(), (float) Math.random(), message.getRollbackAtStep());		
		kafkaProducerService.sendMessageOfTypeStep2("topic2", step2Dto);
	}
	
	@KafkaListener(topics = "topic2", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactoryForStep2")
	public void listenTopic2(@Payload Step2Dto message) {
		
		if(message.getRollbackAtStep() == 2) {
			
			Step1Entity step1Entity = step1Repository.findById(message.getInvoiceId()).get();
			Step1Dto step1Dto = step1Mapper.toDto(step1Entity);
			kafkaProducerService.sendMessageOfTypeStep1("rollbackTopic1", step1Dto);
			return;
		}
		
		Step2Entity step2Entity = step2Mapper.toEntity(message);
		step2Entity = step2Repository.save(step2Entity);
			
		Step3Dto step3Dto = new Step3Dto(step2Entity.getInvoiceId(), ValueGenerator.getRandomAlphaNumeric(10), message.getRollbackAtStep());
		kafkaProducerService.sendMessageOfTypeStep3("topic3", step3Dto);
	}
	
	@KafkaListener(topics = "topic3", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactoryForStep3")
	public void listenTopic3(@Payload Step3Dto message) {
		
		if(message.getRollbackAtStep() == 3) {
			
			Step2Entity step2Entity = step2Repository.findById(message.getFlightId()).get();
			Step2Dto step2Dto = step2Mapper.toDto(step2Entity);
			kafkaProducerService.sendMessageOfTypeStep2("rollbackTopic2", step2Dto);
			return;
		}
		
		Step3Entity step3Entity = step3Mapper.toEntity(message);		
		step3Entity = step3Repository.save(step3Entity);		
	}
	
	
	@KafkaListener(topics = "rollbackTopic1", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactoryForStep1")
	public void listenRollbackTopic1(@Payload Step1Dto message) {
		
		step1Repository.deleteById(message.getStId());
		return;
	}
	
	@KafkaListener(topics = "rollbackTopic2", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactoryForStep2")
	public void listenRollbackTopic2(@Payload Step2Dto message) {
		
		step2Repository.deleteById(message.getInvoiceId());
		
		Step1Entity step1Entity = step1Repository.findById(message.getInvoiceId()).get();
		Step1Dto step1Dto = step1Mapper.toDto(step1Entity);
		kafkaProducerService.sendMessageOfTypeStep1("rollbackTopic1", step1Dto);
		return;
	}
}
