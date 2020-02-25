package com.example.test.testingHMS.laboratory.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.laboratory.model.ServicePdf;
import com.example.test.testingHMS.laboratory.repository.ServicePdfRepository;
import com.example.test.testingHMS.laboratory.service.ServicePdfService;

@Service
public class ServicePdfServiceImpl implements ServicePdfService
{
	@Autowired
	ServicePdfRepository servicePdfRepository;
	
	
	public String getNextLabId() {
		ServicePdf servicesPdf=servicePdfRepository.findFirstByOrderBySidDesc();
		String nextId=null;
		if(servicesPdf==null)
		{
			nextId="SPDF0000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(servicesPdf.getSid().substring(4));
			nextIntId+=1;
			nextId="SPDF"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	public ServicePdf findBySid(String id)
	{
		return servicePdfRepository.findBySid(id);
	}
	
	public List<ServicePdf> findByRegId(String id)
	{
		return servicePdfRepository.findByRegId(id);
	}
	
	public void save(ServicePdf servicePdf)
	{
		servicePdfRepository.save(servicePdf);
	}
	
	public List<ServicePdf> findByRegAndMeasureName(String regId,String name)
	{
		return servicePdfRepository.findByRegAndMeasureName(regId, name);
	}

}
