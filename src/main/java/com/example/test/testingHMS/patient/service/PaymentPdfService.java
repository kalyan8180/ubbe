package com.example.test.testingHMS.patient.service;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.patient.model.PatientPaymentPdf;

public interface PaymentPdfService {
	
	void save(PatientPaymentPdf pdf);
	
	PatientPaymentPdf getBlankPdf(String regId);
	
	PatientPaymentPdf findById(String id);
	
	public String getNextPdfId();
	
	PatientPaymentPdf findByRegIdAndBillNo(String regId, String billno);
	

}
