package com.example.test.testingHMS.pharmacist.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.pharmacist.model.Location;
@Repository
public interface LocationRepository extends CrudRepository<Location,String> 
{
	
	Location findByLocationName(String name);

}
