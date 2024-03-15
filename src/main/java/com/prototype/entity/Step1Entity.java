package com.prototype.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Step1Entity")
public class Step1Entity {

	@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int stId;
	
	private String name;
	private int age;
}
