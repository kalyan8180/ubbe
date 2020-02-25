package com.example.test.testingHMS.laboratory.service;

import java.util.List;

import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.NotesPdf;

public interface NotesPdfService {
	
	String getNextLabId();
	
	NotesPdf findByNid(String id);
	
	NotesPdf findByRegId(String id);
	
	
	
}
