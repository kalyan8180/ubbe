package com.example.test.testingHMS.ambulance.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.ambulance.model.AmbulancePatientDetails;

@Repository
public interface AmbulancePatientDetailsRepository extends CrudRepository<AmbulancePatientDetails, String>{

	
	AmbulancePatientDetails findFirstByOrderByPatAmbulanceIdDesc();
	
	
	public AmbulancePatientDetails findByPatAmbulanceId(String id);
	
	
	public List<AmbulancePatientDetails> findAll();
	
	}

