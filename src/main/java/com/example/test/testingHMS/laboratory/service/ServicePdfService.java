package com.example.test.testingHMS.laboratory.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.laboratory.model.ServicePdf;

public interface ServicePdfService {
	
	String getNextLabId();
	
	ServicePdf findBySid(String id);
	
	List<ServicePdf> findByRegId(String id);
	
	void save(ServicePdf servicePdf);
	
	List<ServicePdf> findByRegAndMeasureName(String regId,String name);
	
	
}
