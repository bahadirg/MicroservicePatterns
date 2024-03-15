package com.prototype.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Step2Dto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int invoiceId;
	private float price;
	
	private int rollbackAtStep;
}
