package com.prototype.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.prototype.entity.Step3Entity;

@Repository
public interface Step3Repository extends CrudRepository<Step3Entity, Integer> {

}
