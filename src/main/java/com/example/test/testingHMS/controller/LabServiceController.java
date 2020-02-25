package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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

import com.example.test.testingHMS.laboratory.dto.LabServicesDto;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.repository.LabServicesRepository;
import com.example.test.testingHMS.laboratory.serviceImpl.LabServicesServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping(value="/v1/service")
public class LabServiceController {
	
	public static Logger Logger=LoggerFactory.getLogger(LabServiceController.class);
	

	@Autowired
	LabServicesServiceImpl labServicesServiceImpl;
	
	@Autowired
	LabServicesRepository labServicesRepository;

	
	
	@RequestMapping(value="/getid",method=RequestMethod.GET)
	public Map<String, Object> getId() {
		return labServicesServiceImpl.pageRefresh();
		
			
		}
	
	
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public void computeSave(@Valid @RequestBody LabServicesDto labServicesDto,Principal principal){
		
		LabServices labServices=new LabServices();

		BeanUtils.copyProperties(labServicesDto, labServices);
		labServicesServiceImpl.computeSave(labServices, principal);
	}
	
	
	@RequestMapping(value="getService/{serviceName}")
	public List<LabServices> getServices(@PathVariable("serviceName") String serviceName){
		return labServicesServiceImpl.findByServiceName(serviceName);
	}
	
	
	
	@RequestMapping(value="/list")
	public List<Object> getList(){
		
		return labServicesServiceImpl.findServices();
	}
	
	
	
	@RequestMapping(value="/update/{serviceName}",method=RequestMethod.POST)
	public void updateService(@RequestBody LabServicesDto labServicesDto,@PathVariable("serviceName") String serviceName,Principal principal) {
		
		LabServices labServices=new LabServices();
		BeanUtils.copyProperties(labServicesDto, labServices);
		
		labServicesServiceImpl.updateService(labServices, principal,serviceName);
	}
	
	@RequestMapping(value="/getservices",method=RequestMethod.GET)
	public List<LabServices> getLabServices() 
	{
		List<LabServices> labservices=labServicesServiceImpl.findAll();
		return labservices;
		
	}
	
	@RequestMapping(value="/getservicelist")
	public Map<String, Object> getServicesList(){
		
		return labServicesServiceImpl.getServicesForDropdown();
	}
	
	
	
	
	
}
