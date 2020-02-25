package com.example.test.testingHMS.patient.service;

import java.util.List;

import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;




public interface PaymentService 
{
	List<PatientPayment> findByPatientRegistration(String regId,String status);


}
