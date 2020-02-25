package com.example.test.testingHMS.pharmacist.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSalesIds;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesPaymentPdf;

public interface SalesService 
{
	String getNextSaleNo();
	
	public String getNextBillNo();
	
	SalesPaymentPdf computeSave(Sales sales,Principal principal);
	
	List<Sales> findByBillNo(String id);
	
	List<Sales> findByPatientRegistration(PatientRegistration patientRegistration);
	
	/*
	 * Modified code for sales
	 */
	public List<Map<String, String>> findMedicineDetailsModified(List<Map<String,String>> medicine);
	
	public RefSalesIds findMedicineDetails(String medicine);
	
	public List<Object> getBillIds();
	
	//new code to get available medicine for next sales
	public List<Sales> findByBatchAndMedicine(String batch,String medicine);
	
	List<Sales> findByName(String medName);
	
	List<Sales> findByPaymentTypeAndPatientRegistration(String payment,PatientRegistration reg);
	
	List<Sales> findByPatientRegistrationAndPaymentType(PatientRegistration patientRegistration,String paymentType);


	SalesPaymentPdf generateOutStandingSalesReport(String regId, Principal principal);

	Iterable<Sales> displaySalesReturnList(String days);

	List<Object> getStockDetails(Map<String, String> stockDetails);


	
	

	

}
