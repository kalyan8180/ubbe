package com.example.test.testingHMS.patient.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.repository.PaymentPdfRepository;
import com.example.test.testingHMS.patient.service.PaymentPdfService;

@Service
public class PaymentPdfServiceImpl implements PaymentPdfService{

	@Autowired
	PaymentPdfRepository paymentPdfRepository;
	
	@Override
	public void save(PatientPaymentPdf pdf) {
		paymentPdfRepository.save(pdf);
		
	}

	public PatientPaymentPdf findById(String id) {
		
		
		return paymentPdfRepository.findByPid(id);
	}
	
	public String getNextPdfId() 
	{
		PatientPaymentPdf patientPaymentPdf=paymentPdfRepository.findFirstByOrderByPidDesc();
		String nextId=null;
		if(patientPaymentPdf==null)
		{
			nextId="PDF0000001";
		}
		else
		{
			String lastUmr=patientPaymentPdf.getPid();
			
			int umrIntId=Integer.parseInt(nextId=lastUmr.substring(3));
			umrIntId+=1;
			nextId="PDF"+String.format("%07d",umrIntId );
		}
		return nextId;
	}
	
	public PatientPaymentPdf getBlankPdf( String regId)
	{
		return paymentPdfRepository.getBlankPdf(regId);
		
	}

	@Override
	public PatientPaymentPdf findByRegIdAndBillNo(String regId, String billno) {
		return paymentPdfRepository.findByRegIdAndBillNo(regId, billno);
	}

	
	
}
