package com.example.test.testingHMS.pharmacist.service;

import java.security.Principal;
import java.util.List;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;

public interface SalesReturnService 
{
	String getNextReturnSaleNo();
	
	
	String getNextReturnMasterSaleNo();
	
	SalesPaymentPdf computeSave(SalesReturn salesReturn,Principal principal);
	

	List<SalesReturn> findByMasterSaleNo(String id);


	List<Object> displaySalesReturnList(int days);
	
	List<SalesReturn> findBySalesReturnPatientRegistration(PatientRegistration patientRegistration);




	SalesPaymentPdf createIpPharmacyReturn(SalesReturn salesReturn, String regId, Principal principal);
	

	List<Object> displaySalesReturnList(String days);

}
