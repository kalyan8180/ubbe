package com.example.test.testingHMS.patient.service;

import java.util.Optional;

import com.example.test.testingHMS.patient.model.PatientTypes;



public interface PatientTypeService 
{
	public PatientTypes save(PatientTypes patientTypes);
	
	public Optional<PatientTypes> findById(Long id);
	
	public void delte(Long id);
	
	public PatientTypes update(PatientTypes patientTypes);
	
	public PatientTypes findByPType(String name);
	
	public Iterable<PatientTypes> findAll();
}
