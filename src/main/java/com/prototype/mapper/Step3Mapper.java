package com.prototype.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.prototype.dto.Step3Dto;
import com.prototype.entity.Step3Entity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Step3Mapper {

	Step3Dto toDto(Step3Entity step3Entity);
	
	Step3Entity toEntity(Step3Dto step3Dto);
}
