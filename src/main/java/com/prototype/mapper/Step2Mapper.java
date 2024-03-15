package com.prototype.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.prototype.dto.Step2Dto;
import com.prototype.entity.Step2Entity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Step2Mapper {

	Step2Dto toDto(Step2Entity step2Entity);
	
	Step2Entity toEntity(Step2Dto step2Dto);
}
