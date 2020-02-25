package com.example.test.testingHMS.user.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.user.model.DoctorSpecialization;

@Repository
public interface DoctorSpecializationRepository extends CrudRepository<DoctorSpecialization, Long>{
	
	DoctorSpecialization findBySpecName(String name);
	
	List<DoctorSpecialization> findAll();

}
