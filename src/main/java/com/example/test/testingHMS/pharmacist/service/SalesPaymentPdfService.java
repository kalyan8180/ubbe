package com.example.test.testingHMS.pharmacist.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;

public interface SalesPaymentPdfService 
{

	String getNextId();
	
	void save(SalesPaymentPdf salesPaymentPdf);
	
	SalesPaymentPdf findByPid(String id);
	
	List<SalesPaymentPdf> getAllReport(String regId);	
	
	public SalesPaymentPdf getOspPdf(String ospId); 
	
	List<SalesPaymentPdf> getReturnPaymentPdfList(String billNo);

}
