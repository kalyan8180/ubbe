package com.example.test.testingHMS.laboratory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.laboratory.model.NurseLaboratory;


@Repository
public interface NurseLaboratoryRepository extends CrudRepository<NurseLaboratory,String>{

	
	
}
