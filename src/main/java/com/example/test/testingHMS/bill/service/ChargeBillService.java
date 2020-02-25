package com.example.test.testingHMS.bill.service;

import java.util.List;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Sales;

public interface ChargeBillService {
	
	String getNextId();
	
	String getNextBillNo();
	
	ChargeBill findBySaleId(Sales sale);
	
	List<ChargeBill> findByPatRegIdAndNetAmountNot(PatientRegistration patientRegistration,float amt);
	
	void save(ChargeBill bill);
	
	ChargeBill findByChargeBillId(String id);
	
	ChargeBill findByServiceId(LabServices service);
	
	List<ChargeBill> findByPatRegIdAndPaid(PatientRegistration patientRegistration,String paid);

	ChargeBill findBySaleIdAndPaymentType(Sales sales,String paymentType);
	
	ChargeBill findByLabIdAndPaymentType(LaboratoryRegistration lab,String paymentType);

	ChargeBill findByServiceIdAndPaymentType(LabServices service,String paymentType);
	
	ChargeBill findByLabId(LaboratoryRegistration laboratoryRegistration);

	List<ChargeBill> findAllLab(String regId);
	
	List<ChargeBill> findDueBill(String regId);
	
	List<ChargeBill> findByPatRegIdAndIpSettledFlag(PatientRegistration patientRegistration,String ipSettledFlag);

}
