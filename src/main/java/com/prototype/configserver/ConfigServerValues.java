package com.prototype.configserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ConfigServerValues {
	
	@Value("${dummy.key.1}")
    private String dummyKey1;

	public String getDummyKey1() {
		return dummyKey1;
	}
}
