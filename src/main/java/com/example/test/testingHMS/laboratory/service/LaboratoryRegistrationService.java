package com.example.test.testingHMS.laboratory.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.patient.model.PatientPaymentPdf;
import com.example.test.testingHMS.patient.model.PatientRegistration;

public interface LaboratoryRegistrationService {
	
	void deleteByLabRegId(String labRegId);
	
	String getNextLabId();
	
	String getNextInvoice();
	
	String getNextBillNo();
	
	PatientPaymentPdf computeSave(LaboratoryRegistration laboratoryRegistration,Principal p);

	List<LaboratoryRegistration> findAll();
	
	LaboratoryRegistration findByLabRegId(String id);
	
	List<LaboratoryRegistration> findBill(String regId, String invoice);
	
	List<LaboratoryRegistration> particularPatientRecordScroll(Object fromDate,Object toDate,String userName,String regId);
	
	
	public List<Object> getService();
	
	public Map<String, String> getServiceCost(String name,String regId);
	
	List<LaboratoryRegistration> findByLaboratoryPatientRegistration(PatientRegistration patientRegistration);
	
	List<LaboratoryRegistration> findByPaymentTypeAndLaboratoryPatientRegistration(String payment,PatientRegistration reg);
	
	List<LaboratoryRegistration> findUserWiseIpOpDetailed(Object fromDate,Object toDate,String userName, String status);

	List<Object> getCancelSErvices(String billNo);


	
	
	

	
}
