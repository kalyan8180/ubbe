package com.example.test.testingHMS.patient.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.InsuranceCompany;
import com.example.test.testingHMS.patient.repository.InsuranceCompanyRepository;
import com.example.test.testingHMS.patient.service.InsuranceCompanyService;

@Service
public class InsuranceCompanyServiceImpl implements InsuranceCompanyService{

	
	@Autowired
	private InsuranceCompanyRepository insuranceCompanyRepository;
	
	@Override
	public List<InsuranceCompany> findAll() {
		return insuranceCompanyRepository.findAll();
	}

}
