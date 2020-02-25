package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.laboratory.dto.MasterCheckUpRegistrationDto;
import com.example.test.testingHMS.laboratory.dto.MasterCheckupServiceDto;
import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;
import com.example.test.testingHMS.laboratory.model.MasterCheckupService;
import com.example.test.testingHMS.laboratory.serviceImpl.MasterCheckUpRegistrationServiceImpl;
import com.example.test.testingHMS.laboratory.serviceImpl.MasterCheckupServiceServiceImpl;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/mastercheckup")

public class MasterCheckUpController {
	
	@Autowired
	MasterCheckupServiceServiceImpl masterCheckupServiceServiceImpl;
	
	@Autowired
	MasterCheckUpRegistrationServiceImpl masterCheckUpRegistrationServiceImpl;
	
	

	@RequestMapping(value="/getid",method=RequestMethod.GET)
	public List<Object> getId() {
		return masterCheckupServiceServiceImpl.pageRefresh();
	}	
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public void computeSave(@RequestBody MasterCheckupServiceDto masterCheckupServiceDto,Principal principal) {
		MasterCheckupService masterCheckupService=new MasterCheckupService();
		BeanUtils.copyProperties(masterCheckupServiceDto, masterCheckupService);
		
		masterCheckupServiceServiceImpl.computeSave(masterCheckupService,principal);
	}
	
	
	@RequestMapping(value="/getAll")
	public List<Object> getAll(){
		
		
		return masterCheckupServiceServiceImpl.getAll();
	}

	
	@RequestMapping(value="getServices/{checkupId}")
	public List<MasterCheckupService> getServices(@PathVariable("checkupId") String checkupId){
		
		return masterCheckupServiceServiceImpl.findByCheckupId(checkupId);
	}
	
	@RequestMapping(value="/create")
	public List<Object> getMasterCheckUp() {
		return masterCheckUpRegistrationServiceImpl.pageRefresh();
	}
	
	@RequestMapping(value="/getcost",method=RequestMethod.POST)
	List<Object> getCostForService(@RequestBody Map<String, String> serviceInfo){
		List<Object> list=new ArrayList<Object>();
		String serviceName=serviceInfo.get("serviceName");
		List<MasterCheckupService> masterCheckupService=masterCheckupServiceServiceImpl.findByMasterServiceName(serviceName);
		Map<String, Float> map=new HashMap<String, Float>();
		map.put("cost", masterCheckupService.get(0).getCost());
		list.add(map);
		return list;
	}
	
	
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public SalesPaymentPdf registerMastercheckupServices(@RequestBody MasterCheckUpRegistrationDto masterCheckUpRegistrationDto,Principal principal) {
		MasterCheckUpRegistration masterCheckUpRegistration=new MasterCheckUpRegistration();
		BeanUtils.copyProperties(masterCheckUpRegistrationDto, masterCheckUpRegistration);
		return masterCheckUpRegistrationServiceImpl.registerMastercheckupServices(masterCheckUpRegistration,principal);
		
	}
	
	

	@RequestMapping(value="/get/servicenames",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public List<Object> getservicenames(@RequestBody Map<String, String> mapInfo)
	{
		 return masterCheckupServiceServiceImpl.getservicenames(mapInfo);
}

	
	
	
	
}