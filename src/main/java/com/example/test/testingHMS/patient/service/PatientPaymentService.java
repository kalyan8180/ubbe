package com.example.test.testingHMS.patient.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;

public interface PatientPaymentService {
	
	List<PatientPayment> findByModeOfPaymantAndPatientRegistration(String name,PatientRegistration reg);
	
	List<PatientPayment>findDueBill(String regId);
	
	String findNextBillNo();
	
	List<PatientPayment> findAll();

	
	PatientPayment save(PatientPayment patientPayment);
	
	Set<PatientPayment> findByPatientRegistration(PatientRegistration reg);
	
	PatientPayment findPatientByRegFee(String regId,String typeOfCharge);

	
	PatientPayment findOneByPatientRegistrationAndTypeOfCharge(PatientRegistration reg,String type);
	

	List<PatientPayment> findByTypeOfChargeAndPatientRegistration(String typeOfCharge,PatientRegistration patientRegistration);

}
