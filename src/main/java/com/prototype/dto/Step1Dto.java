package com.prototype.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Step1Dto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int stId;
	private String name;
	private int age;
	
	
	
	private int rollbackAtStep;
}
