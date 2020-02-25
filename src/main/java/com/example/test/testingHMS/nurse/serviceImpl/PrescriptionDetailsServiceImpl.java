package com.example.test.testingHMS.nurse.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.nurse.repository.PrescriptionDetailsRepository;
import com.example.test.testingHMS.nurse.service.PrescriptionDetailsService;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientDetailsServiceImpl;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;


@Service
public class PrescriptionDetailsServiceImpl implements PrescriptionDetailsService 
{
	@Autowired
	PatientDetailsServiceImpl patientDetailsServiceImpl;
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;
	
	@Autowired
	PrescriptionDetailsRepository prescriptionDetailsRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	PrescriptionDetailsServiceImpl prescriptionDetailsServiceImpl;

	@Override
	public PrescriptionDetails save(PrescriptionDetails prescriptionDetails) {
		return prescriptionDetailsRepository.save(prescriptionDetails);
	}

	@Override
	public void computeSave(PrescriptionDetails prescriptionDetails) 
	{
		prescriptionDetails.setPrescriptionId(prescriptionDetailsServiceImpl.generatePrescriptionId());
		PatientRegistration patientRegistration=patientRegistrationServiceImpl.findByRegId(prescriptionDetails.getRegId());
		if(patientRegistration==null)
		{
			System.out.println("empty value");
		}
		
		String name=patientRegistration.getPatientDetails().getConsultant();
		String[] s=name.split(" ");
		String firstName=s[0]+" "+s[1];
		System.out.println(firstName);
		String lastName=s[2];
		System.out.println(lastName);
		//User user=userServiceImpl.findByFirstNameAndLastName(firstName, lastName);
		prescriptionDetails.setDoctorName(firstName+" "+lastName);
		Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());
		prescriptionDetails.setCreatedAt(timestamp);
		prescriptionDetails.setPatientRegistration(patientRegistration);
		
		//After security
		//prescriptionDetails.setUserDetails(userDetails);
		
		prescriptionDetails.setFileDownloadUri(ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/v1/doctor/viewFile/")
	                .path(prescriptionDetails.getPrescriptionId())
	                .toUriString());
		
		/* String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(prescriptionDetails.getPrescriptionId())
	                .toUriString();
		*/
		 prescriptionDetailsRepository.save(prescriptionDetails);
		
		 
		
	}
	
	public String generatePrescriptionId()
	{

		PrescriptionDetails prescriptionDetails=prescriptionDetailsRepository.findFirstByOrderByPrescriptionIdDesc();
		String nextId=null;
		if(prescriptionDetails==null)
		{
			nextId="PRE000001";
		}
		else
		{
			String lastId=prescriptionDetails.getPrescriptionId();
			int lastIntId=Integer.parseInt(lastId.substring(3));
			lastIntId+=1;
			nextId="PRE"+String.format("%06d", lastIntId);
		}
		return nextId;
	}

	public Iterable<PrescriptionDetails> findAll()
	{
		return prescriptionDetailsRepository.findAll();
	}
	
	public PrescriptionDetails getFile(String id)
	{
		return prescriptionDetailsRepository.findByPrescriptionId(id);
		
	}
	
	
	
	public PrescriptionDetails findByPatientRegistration(PatientRegistration patientRegistration)
	{
		return prescriptionDetailsRepository.findByPatientRegistration(patientRegistration);
	}

	@Override
	public PrescriptionDetails findByRegId(String regId) {
		return prescriptionDetailsRepository.findByRegId(regId);
	}
	
	
}
