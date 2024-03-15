package com.prototype.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Step2Entity")
public class Step2Entity {
	
	
	@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int invoiceId;
	private float price;
}
