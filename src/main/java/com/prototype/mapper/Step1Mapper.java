package com.prototype.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.prototype.dto.Step1Dto;
import com.prototype.entity.Step1Entity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Step1Mapper {

	Step1Dto toDto(Step1Entity step1Entity);
	
	Step1Entity toEntity(Step1Dto step1Dto);
}
