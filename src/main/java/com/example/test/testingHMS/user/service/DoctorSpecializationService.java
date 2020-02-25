package com.example.test.testingHMS.user.service;

import java.util.List;

import com.example.test.testingHMS.user.model.DoctorSpecialization;

public interface DoctorSpecializationService {
	
	DoctorSpecialization findBySpecName(String name);
	
	List<DoctorSpecialization> findAll();



}
