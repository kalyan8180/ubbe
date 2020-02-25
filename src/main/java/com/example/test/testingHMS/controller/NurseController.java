package com.example.test.testingHMS.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bed.serviceImpl.RoomBookingDetailsServiceImpl;
import com.example.test.testingHMS.bed.serviceImpl.RoomDetailsServiceImpl;
import com.example.test.testingHMS.doctor.dto.RefDoctorDetails;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.model.NotesPdf;
import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.serviceImpl.NotesDetailsServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.NotesPdfServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.ServicePdfServiceImpl;
import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.nurse.serviceImpl.PrescriptionDetailsServiceImpl;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.helper.RefRaisePharmacy;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/nurse")
public class NurseController
{
	public static Logger Logger=LoggerFactory.getLogger(NurseController.class);
	
	
	@Autowired
	PrescriptionDetailsServiceImpl prescriptionDetailsServiceImpl;
	
	@Autowired
	RefRaisePharmacy refRaisePharmacy;
	
	@Autowired
	ServicePdfServiceImpl servicePdfServiceImpl;
	
	@Autowired
	NotesDetailsServiceImpl notesDetailsServiceImpl;
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;
	
	@Autowired
	RefDoctorDetails refDoctorDetails;
	
	
	@Autowired
	NotesPdfServiceImpl notesPdfServiceImpl;
	
	@Autowired
	RoomBookingDetailsServiceImpl roomBookingDetailsServiceImpl;
	
	@Autowired
	RoomDetailsServiceImpl roomDetailsServiceImpl;
	

	@RequestMapping(value="/getAll",method=RequestMethod.GET)
	public List<RefDoctorDetails> getAll(Pageable pageable)
	{
		List<RefDoctorDetails> refDoctorDetailsList=new ArrayList<>();
		List<PatientRegistration> patientRegistration=patientRegistrationServiceImpl.findAll();
	

		
		for(PatientRegistration patientRegistrationList:patientRegistration) 
		{
			RefDoctorDetails refDoctorDetailsInfo=new RefDoctorDetails();
			
			NotesPdf notesPdf=notesPdfServiceImpl.findByRegId(patientRegistrationList.getRegId());
			List<ServicePdf> servicePdf=servicePdfServiceImpl.findByRegId(patientRegistrationList.getRegId());
			List<Map<String,String>> urlInfo=new ArrayList<>();
			
			
			if(notesPdf!=null)
			{
				refDoctorDetailsInfo.setNotes(notesPdf.getFileuri());
			}
			if(servicePdf!=null)
			{
				for(ServicePdf servicePdfInfo:servicePdf)
				{
					Map<String,String> url=new HashMap<>();
					url.put(servicePdfInfo.getFileName(),servicePdfInfo.getFileuri());
					urlInfo.add(url);
				}
				refDoctorDetailsInfo.setReport(urlInfo);
				
			}
			refDoctorDetailsInfo.setRegNo(patientRegistrationList.getRegId());
			refDoctorDetailsInfo.setPatientName(patientRegistrationList.getPatientDetails().getFirstName()+" "+patientRegistrationList.getPatientDetails().getLastName());
			refDoctorDetailsInfo.setDoj(patientRegistrationList.getDateOfJoining().toString().substring(0, 10));
			refDoctorDetailsInfo.setDoctorName(patientRegistrationList.getPatientDetails().getConsultant());
			PrescriptionDetails prescriptionDetails=prescriptionDetailsServiceImpl.findByRegId(patientRegistrationList.getRegId());
			if(prescriptionDetails!=null)
			{
				refDoctorDetailsInfo.setPrescription(prescriptionDetails.getFileDownloadUri());
			}
			refDoctorDetailsList.add(refDoctorDetailsInfo);
		}
		return refDoctorDetailsList;
	}
	
	@RequestMapping("/get")
	public List<String> getNurse()
	{
		Iterable<PrescriptionDetails> prescriptionDetails=prescriptionDetailsServiceImpl.findAll();
		List<String> al=new ArrayList<>();
		for(PrescriptionDetails p:prescriptionDetails)
		{
			al.add(p.getFileDownloadUri());
		}
		return al;
	}
	
	
	
	//Changing patient bed by nurse
	@RequestMapping("/bed")
	public void changeBed(@RequestBody Map<String,String> bedInfo) 
	{
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(bedInfo.get("patId"));
		RoomBookingDetails roomBookingDetailsInfo=roomBookingDetailsServiceImpl.findByPatientRegistrationBooking(patientRegistration);
		roomBookingDetailsInfo.setBedNo(bedInfo.get("bedName"));
		RoomDetails roomDetailsInfo=roomDetailsServiceImpl.findByBedName(bedInfo.get("bedName"));
		roomBookingDetailsInfo.setRoomDetails(roomDetailsInfo);
		roomBookingDetailsInfo.setPatientRegistrationBooking(patientRegistration);
		
		roomBookingDetailsServiceImpl.save(roomBookingDetailsInfo);
		
	}
	
	@RequestMapping("/viewFile/{id}")
	public ResponseEntity<ByteArrayResource> viewFile(@PathVariable String id) {
        // Load file from database
		PrescriptionDetails prescriptionDetails = prescriptionDetailsServiceImpl.getFile(id);

        return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(prescriptionDetails.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,String.format("inline; filename=\"" + prescriptionDetails.getFileNamee() + "\""))
                .body(new ByteArrayResource(prescriptionDetails.getFileName()));
    }
	
	@RequestMapping("/downloadFile/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String id) {
        // Load file from database
		PrescriptionDetails prescriptionDetails = prescriptionDetailsServiceImpl.getFile(id);

        return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(prescriptionDetails.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + prescriptionDetails.getFileNamee() + "\"")
                .body(new ByteArrayResource(prescriptionDetails.getFileName()));
    }
	
	// Raise pharmacy get for nurse
		@RequestMapping(value="/raise/pharmacy",method=RequestMethod.GET)
		public List<RefRaisePharmacy> raisePharmacy()
		{
			List<RefRaisePharmacy> displayList=new ArrayList<>();
			List<PatientRegistration> patientRegistration=patientRegistrationServiceImpl.findAll();
			for(PatientRegistration patientRegistrationInfo:patientRegistration)
			{
			RefRaisePharmacy refRaisePharmacy=new RefRaisePharmacy();
			refRaisePharmacy.setRegNo(patientRegistrationInfo.getRegId());
			NotesDetails notesDetails=notesDetailsServiceImpl.findByPatientRegistrationNotes(patientRegistrationInfo);
				if(notesDetails!=null && notesDetails.getPharmacyNotes()!=null)
				{
					refRaisePharmacy.setMedicineList(notesDetails.getPharmacyNotes());
					displayList.add(refRaisePharmacy);
				}
				
			}
			return displayList;
			
		}


}

