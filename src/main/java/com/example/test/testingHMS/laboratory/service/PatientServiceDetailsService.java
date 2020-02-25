package com.example.test.testingHMS.laboratory.service;

import java.util.List;

import com.example.test.testingHMS.laboratory.model.PatientServiceDetails;

public interface PatientServiceDetailsService {
	
	String getNextId();
	
	PatientServiceDetails findByRegId(String id);
	
	void save(PatientServiceDetails patientServiceDetails);
	
	List<PatientServiceDetails> findByPatientServiceAndPatientLabService(String regId,String serviceId);
	
	
}
