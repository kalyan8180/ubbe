package com.example.test.testingHMS.nurse.service;

import java.util.List;

import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;

public interface PrescriptionDetailsService
{
	PrescriptionDetails save(PrescriptionDetails prescriptionDetails);
	
	void computeSave(PrescriptionDetails prescriptionDetails);
	
	public String generatePrescriptionId();
	
	Iterable<PrescriptionDetails> findAll();
	
	public PrescriptionDetails getFile(String id);
	
	PrescriptionDetails findByRegId(String regId);
	
	PrescriptionDetails findByPatientRegistration(PatientRegistration patientRegistration);
	

}
