package com.example.test.testingHMS.pharmacist.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.pharmacist.model.TaxDetails;
@Repository
public interface TaxDetailsRepository extends CrudRepository<TaxDetails,String> 
{
	

}
