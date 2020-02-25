package com.example.test.testingHMS.patient.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.patient.model.MarketingQuestions;


@Repository
public interface MarketingRepository extends CrudRepository<MarketingQuestions,Long>
{
	
	MarketingQuestions findByQuestion(String name);

}
