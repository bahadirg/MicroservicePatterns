package com.prototype.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.prototype.entity.Step2Entity;

@Repository
public interface Step2Repository extends CrudRepository<Step2Entity, Integer> {

}
