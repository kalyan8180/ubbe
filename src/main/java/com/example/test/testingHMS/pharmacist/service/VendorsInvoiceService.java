package com.example.test.testingHMS.pharmacist.service;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.test.testingHMS.pharmacist.helper.RefApprovedListHelper;
import com.example.test.testingHMS.pharmacist.model.VendorsInvoice;

public interface VendorsInvoiceService  
{
	List<VendorsInvoice> findByVendorInvoiceMedicineProcurement(String id);
	
	List<String> findOneInvoice(String id);
	
	String getNextInvoice();
	
	void computeSave(VendorsInvoice vendorsInvoice);

	long findSumOfPaidAmount(String pid);
	
	public List<RefApprovedListHelper> getApprovedProcurement();
	
}
