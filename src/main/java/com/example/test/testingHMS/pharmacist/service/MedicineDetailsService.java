package com.example.test.testingHMS.pharmacist.service;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.test.testingHMS.pharmacist.model.MedicineDetails;

public interface MedicineDetailsService
{
	
	void computeSave(MedicineDetails medicineDetails,Principal p);
	
	String getNextMedId();
	
	List<MedicineDetails> findAll();
	
	MedicineDetails findByName(String name);

}
