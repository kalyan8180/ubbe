package com.example.test.testingHMS.user.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.user.model.DoctorSpecialization;
import com.example.test.testingHMS.user.repository.DoctorSpecializationRepository;
import com.example.test.testingHMS.user.service.DoctorSpecializationService;

@Service
public class DoctorSpecializationServiceImpl implements DoctorSpecializationService{

	@Autowired
	DoctorSpecializationRepository 	doctorSpecializationRepository;

	
	public DoctorSpecialization findBySpecName(String name)
	{
		return doctorSpecializationRepository.findBySpecName(name);
	}
	
	public 	List<DoctorSpecialization> findAll()
	{
		return doctorSpecializationRepository.findAll();
	}



}
