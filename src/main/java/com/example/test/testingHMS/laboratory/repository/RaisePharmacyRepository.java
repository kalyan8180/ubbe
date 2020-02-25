package com.example.test.testingHMS.laboratory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.RaisePharmacy;


@Repository
public interface RaisePharmacyRepository extends CrudRepository<RaisePharmacy,String>{

	
	
}
