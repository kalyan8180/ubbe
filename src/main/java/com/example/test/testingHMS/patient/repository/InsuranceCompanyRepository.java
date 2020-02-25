package com.example.test.testingHMS.patient.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.InsuranceCompany;

@Repository
public interface InsuranceCompanyRepository  extends CrudRepository<InsuranceCompany, Integer>{
	
	List<InsuranceCompany> findAll();

}
