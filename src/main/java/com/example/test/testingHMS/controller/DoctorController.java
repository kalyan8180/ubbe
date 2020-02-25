package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.doctor.dto.RefDoctorDetails;
import com.example.test.testingHMS.doctor.model.RefPrescription;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.model.NotesPdf;
import com.example.test.testingHMS.laboratory.serviceImpl.NotesPdfServiceImpl;
import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.nurse.serviceImpl.PrescriptionDetailsServiceImpl;
import com.example.test.testingHMS.user.serviceImpl.DoctorDetailsServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/doctor")
public class DoctorController 
{
	
	public static Logger Logger=LoggerFactory.getLogger(DoctorController.class);
	
	
	@Autowired
	DoctorDetailsServiceImpl doctorDetailsServiceImpl;
	
	@Autowired
	NotesPdfServiceImpl notesPdfServiceImpl;
	
	@Autowired
	PrescriptionDetailsServiceImpl prescriptionDetailsServiceImpl;
	
	@RequestMapping(value="/getAll",method=RequestMethod.GET)
	public List<RefDoctorDetails> getAll(Principal principal)
	{
		return doctorDetailsServiceImpl.getAll(principal);
			
	}

	@RequestMapping(value="/create/notes",method=RequestMethod.POST)
	public NotesPdf createnotes(@RequestBody NotesDetails notesDetails)
	{
		 return doctorDetailsServiceImpl.createNotes(notesDetails);
		
	}

	/*
	 * To create pharmacy notes
	 */
	@RequestMapping(value="/create/pharmacyNotes",method=RequestMethod.POST)
	public void createPrescriptionNotes(@RequestBody NotesDetails notesDetails)
	{
		doctorDetailsServiceImpl.createPrescriptionNotes(notesDetails);
	}
	
	//to get notes pdf
	@RequestMapping(value="/notes/{regId}",method=RequestMethod.POST)
	public NotesPdf getNotes(@PathVariable String regId)
	{
		return doctorDetailsServiceImpl.getNotes(regId);
			
	}
	
	/*
	 * To get prescription 
	 */
	@RequestMapping(value="/prescription/{regId}",method=RequestMethod.GET)
	public PrescriptionDetails create(@PathVariable String regId)
	{
		PrescriptionDetails prescriptionDetails=prescriptionDetailsServiceImpl.findByRegId(regId);
		return prescriptionDetails;
	}
	
	/*
	 * To write prescription
	 */
	@RequestMapping(value="/prescription",method=RequestMethod.POST)
	public PrescriptionDetails create(@RequestBody RefPrescription refPrescription)
	{
		return doctorDetailsServiceImpl.create(refPrescription);
	}
	
	/*
	 * To get notes pdf
	 */
	@RequestMapping("/viewFile/{id}")
	public ResponseEntity<ByteArrayResource> viewFile(@PathVariable String id) {
        // Load file from database
		NotesPdf notesPdf = notesPdfServiceImpl.findByNid(id);

        return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType("application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION,String.format("inline; filename=\"" + notesPdf.getFileName() + "\""))
                .body(new ByteArrayResource(notesPdf.getData()));
    }
	
}
