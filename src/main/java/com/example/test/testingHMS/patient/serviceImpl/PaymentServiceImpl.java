package com.example.test.testingHMS.patient.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.PatientPayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.repository.PaymentRepository;
import com.example.test.testingHMS.patient.service.PaymentService;


@Service
public class PaymentServiceImpl implements PaymentService
{
	@Autowired
	PaymentRepository paymentRepository;

	@Override
	public List<PatientPayment> findByPatientRegistration(String regId,String status) {
		return paymentRepository.findByPatientRegistration(regId,status);
	}
	
	
	
}
