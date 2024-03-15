package com.prototype.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.prototype.dto.Step1Dto;
import com.prototype.dto.Step2Dto;
import com.prototype.dto.Step3Dto;

@Service
@DependsOn({"kafkaTemplate1", "kafkaTemplate2", "kafkaTemplate3"}) 
public class KafkaProducerService {

	@Autowired
	@Qualifier("kafkaTemplate1")                                                                                 
	KafkaTemplate<String, Step1Dto> kafkaTemplate1;                                       								
	
	@Autowired
	@Qualifier("kafkaTemplate2")                                                                                 
	KafkaTemplate<String, Step2Dto> kafkaTemplate2;
	
	@Autowired
	@Qualifier("kafkaTemplate3")                                                                                 
	KafkaTemplate<String, Step3Dto> kafkaTemplate3;
	
	public void sendMessageOfTypeStep1(String topic, Step1Dto message) {                                                  
		kafkaTemplate1.send(topic, message);                                                                         
	}
	
	public void sendMessageOfTypeStep2(String topic, Step2Dto message) {                                                  
		kafkaTemplate2.send(topic, message);                                                                         
	}
	
	public void sendMessageOfTypeStep3(String topic, Step3Dto message) {                                                  
		kafkaTemplate3.send(topic, message);                                                                         
	}
}
