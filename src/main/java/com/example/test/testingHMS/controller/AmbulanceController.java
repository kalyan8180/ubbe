package com.example.test.testingHMS.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.ambulance.dto.AmbulancePatientDetailsDTO;
import com.example.test.testingHMS.ambulance.model.AmbulancePatientDetails;
import com.example.test.testingHMS.ambulance.model.AmbulanceServices;
import com.example.test.testingHMS.ambulance.repository.AmbulancePatientDetailsRepository;
import com.example.test.testingHMS.ambulance.repository.AmbulanceServicesRepository;
import com.example.test.testingHMS.ambulance.serviceImpl.AmbulancePatientDetailsServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/ambulance")

public class AmbulanceController {
	
	public static Logger Logger=LoggerFactory.getLogger(AmbulanceController.class);
	
	
	@Autowired
	AmbulanceServicesRepository ambulanceServicesRepository;
	
	@Autowired
	AmbulancePatientDetailsRepository ambulancePatientDetailsRepository;
	
	@Autowired
	AmbulancePatientDetailsServiceImpl ambulancePatientDetailsServiceImpl;
	
	
	
	@RequestMapping(value="create")
	public List<Object> getPageLoad(){
		
		return ambulancePatientDetailsServiceImpl.pageLoad();
	}
	
	
	
	@RequestMapping(value="/getambulance")
	public List<AmbulanceServices> getAmbulances(){
		
		List<AmbulanceServices> ambulanceServices=ambulanceServicesRepository.getAmbulanceStatus();
		
		
		return ambulanceServices;
	}
	
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public void create(@RequestBody AmbulancePatientDetailsDTO ambulancePatientDetailsDTO){
		
		AmbulancePatientDetails ambulancePatientDetails=new AmbulancePatientDetails();
		BeanUtils.copyProperties(ambulancePatientDetailsDTO, ambulancePatientDetails);
		
		ambulancePatientDetails.setPatAmbulanceId(ambulancePatientDetailsServiceImpl.getNextLabId());
		
		
		
		ambulancePatientDetailsServiceImpl.computeSave(ambulancePatientDetails);	
		
		
	}
	
	@RequestMapping(value="/update/{id}",method=RequestMethod.PUT)
	public void update(@RequestBody AmbulancePatientDetailsDTO ambulancePatientDetailsDTO,@PathVariable("id") String id){
		AmbulancePatientDetails ambulancePatientDetails=new AmbulancePatientDetails();
		BeanUtils.copyProperties(ambulancePatientDetailsDTO, ambulancePatientDetails);
		
		ambulancePatientDetails.setPatAmbulanceId(id);
			
		ambulancePatientDetailsServiceImpl.update(ambulancePatientDetails);
		
	}
	
	@RequestMapping(value="getAll",method=RequestMethod.GET)
	public List<AmbulancePatientDetails> getAll(){
		
		return ambulancePatientDetailsServiceImpl.findAll();
		
	}
	
	
		
}
