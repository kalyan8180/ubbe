package com.example.test.testingHMS.pharmacist.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.repository.SalesPaymentPdfRepository;
import com.example.test.testingHMS.pharmacist.service.SalesPaymentPdfService;
@Service
public class SalesPaymentPdfServiceImpl implements SalesPaymentPdfService 
{
	@Autowired
	SalesPaymentPdfRepository salesPaymentPdfRepository;

	@Override
	public String getNextId() 
	{
		SalesPaymentPdf salesPayment=salesPaymentPdfRepository.findFirstByOrderByPidDesc();
		String nextId=null;
		if(salesPayment==null)
		{
			nextId="SID00000001";
		}
		else
		{
			int nextIntId=Integer.parseInt(salesPayment.getPid().substring(3));
			nextIntId+=1;
			nextId="SID"+String.format("%08d", nextIntId);
		}
		return nextId;
	}

	@Override
	public void save(SalesPaymentPdf salesPaymentPdf) {
		 salesPaymentPdfRepository.save(salesPaymentPdf);		
	}
	
	public SalesPaymentPdf findByPid(String id)
	{
		return salesPaymentPdfRepository.findByPid(id);
	}

	@Override
	public List<SalesPaymentPdf> getAllReport(String regId) {
		return salesPaymentPdfRepository.getAllReport(regId);
	}
	
	public SalesPaymentPdf getSalesPdf(String billNo) {
		return salesPaymentPdfRepository.getSalesPdf(billNo);
	}
	

	
	@Override
	public SalesPaymentPdf getOspPdf(String ospId) {
		return salesPaymentPdfRepository.getOspPdf(ospId);
	}
	
	public SalesPaymentPdf findByFileName(String name)
	{
		return salesPaymentPdfRepository.findByFileName(name);
	}
	
	public SalesPaymentPdf getReturnPaymentPdf(String billNo) {
		return salesPaymentPdfRepository.getReturnPaymentPdf(billNo);
	}
	
	public SalesPaymentPdf getFinalAdvancePdf(String regId) {
		return salesPaymentPdfRepository.getFinalAdvancePdf(regId);
	}

	@Override
	public List<SalesPaymentPdf> getReturnPaymentPdfList(String billNo) {
		return salesPaymentPdfRepository.getReturnPaymentPdfList(billNo);
	}
    
}
