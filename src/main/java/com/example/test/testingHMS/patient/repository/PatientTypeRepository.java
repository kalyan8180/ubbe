package com.example.test.testingHMS.patient.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.PatientTypes;



@Repository
public interface PatientTypeRepository extends CrudRepository<PatientTypes,Long>
{
	
	PatientTypes findByPType(String name);

	Iterable<PatientTypes> findAll();
}
