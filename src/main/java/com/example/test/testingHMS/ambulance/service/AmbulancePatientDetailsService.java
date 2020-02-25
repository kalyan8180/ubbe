package com.example.test.testingHMS.ambulance.service;

import java.util.List;

import com.example.test.testingHMS.ambulance.model.AmbulancePatientDetails;

public interface AmbulancePatientDetailsService {
	
	
	
	public 	List<Object> pageLoad();
	
	public void computeSave(AmbulancePatientDetails ambulancePatientDetails);
	
	public void update(AmbulancePatientDetails ambulancePatientDetails);
	
	public List<AmbulancePatientDetails> findAll();


}

