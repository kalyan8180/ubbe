package com.example.test.testingHMS.patient.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.model.MarketingQuestions;
import com.example.test.testingHMS.patient.repository.MarketingRepository;
import com.example.test.testingHMS.patient.service.MarketingQuestionsService;


@Service
public class MarketingQuestionsServiceImpl implements MarketingQuestionsService
{
	@Autowired
	MarketingRepository repo;
	
	public MarketingQuestions save(MarketingQuestions marketingQuestions)
	{
		return repo.save(marketingQuestions);
	}
	
	public Optional<MarketingQuestions> findById(Long id)
	{
		return repo.findById(id);
	}
	
	public void delte(Long id)
	{
		 repo.deleteById(id);
	}
	
	public MarketingQuestions update(MarketingQuestions marketingQuestions)
	{
		return repo.save(marketingQuestions);
	}
	
	/*public List<PatientRegistration> findByVPatientDetailsContaining(Long pid)
	{
		return repo.findByVPatientDetailsContaining(pid);
	}*/
	
	public MarketingQuestions findByQuestion(String name)
	{
		return repo.findByQuestion(name);
	}
	
}
