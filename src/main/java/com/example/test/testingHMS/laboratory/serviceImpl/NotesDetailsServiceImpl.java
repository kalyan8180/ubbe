package com.example.test.testingHMS.laboratory.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.repository.NotesDetailsRepository;
import com.example.test.testingHMS.laboratory.service.NotesDetailsService;
import com.example.test.testingHMS.patient.model.PatientRegistration;

@Service
public class NotesDetailsServiceImpl implements NotesDetailsService {
	
	public static Logger Logger=LoggerFactory.getLogger(NotesDetailsServiceImpl.class);
	
	
	@Autowired
	NotesDetailsRepository notesDetailsRepository;
	
	public String getNextNoteId()
	{
		NotesDetails notesDetails=notesDetailsRepository.findFirstByOrderByNoteIdDesc();
		String nextId=null;
		if(notesDetails==null)
		{
			nextId="NID0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(notesDetails.getNoteId().substring(3));
			nextIntId+=1;
			nextId="NID"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	
	
	public NotesDetails findByPatientRegistrationNotes(PatientRegistration patientRegistration)
	{
		return notesDetailsRepository.findByPatientRegistrationNotes(patientRegistration);
	}
}
