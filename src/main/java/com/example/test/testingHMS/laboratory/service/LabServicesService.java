package com.example.test.testingHMS.laboratory.service;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.laboratory.model.LabServices;

public interface LabServicesService {
	
	List<LabServices> findByServiceName(String name);
	
	LabServices findPriceByType(String name,String type,String room);
	
	LabServices findByServiceNameAndPatientType(String serviceName,String patName);
	
	List<LabServices> findOnlyLab(String lab);
	
	List<LabServices> servicesForInptient(String type);

	LabServices findByServiceId(String serviceId);
	
	List<Object> findServices();
	
	void updateService(LabServices labServices, Principal principal, String serviceName);
	
	List<LabServices> findAll();
	
	List<LabServices> findOnlyOthers();
	
	String getNextId();
	
	List<LabServices> getOspServices(String patientType);
	
	public void computeSave(LabServices labServices,Principal principal);
	
	
	
}
