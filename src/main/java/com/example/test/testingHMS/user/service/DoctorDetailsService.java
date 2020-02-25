package com.example.test.testingHMS.user.service;

import java.security.Principal;
import java.util.List;

import com.example.test.testingHMS.doctor.dto.RefDoctorDetails;
import com.example.test.testingHMS.doctor.model.RefPrescription;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.model.NotesPdf;
import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.user.model.DoctorDetails;


public interface DoctorDetailsService 
{
	String getNextId();
	
	public List<RefDoctorDetails> getAll(Principal principal);
	
	public NotesPdf createNotes(NotesDetails notesDetails);
	
	public void createPrescriptionNotes(NotesDetails notesDetails);
	
	public NotesPdf getNotes(String regId);
	
	public PrescriptionDetails create(RefPrescription refPrescription);
	
	public List<DoctorDetails> findBySpecilization(String specialization);
	
	DoctorDetails findByDrRegistrationo(String regNo);
}
