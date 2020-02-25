package com.example.test.testingHMS.laboratory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;

@Repository
public interface MasterCheckUpRegistrationRepository extends CrudRepository<MasterCheckUpRegistration, String>{
	
	MasterCheckUpRegistration findFirstByOrderByCheckUpRegistrationIdDesc();
	
	MasterCheckUpRegistration findFirstByOrderByBillNoDesc();
}
