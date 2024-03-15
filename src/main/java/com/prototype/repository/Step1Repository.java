package com.prototype.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.prototype.entity.Step1Entity;

@Repository
public interface Step1Repository extends CrudRepository<Step1Entity, Integer> {

}
