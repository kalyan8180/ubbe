package com.example.test.testingHMS.laboratory.service;

import java.util.List;

import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;

public interface NotesDetailsService {
	
	String getNextNoteId();
	
	
	
	NotesDetails findByPatientRegistrationNotes(PatientRegistration patientRegistration);
	
	
}
