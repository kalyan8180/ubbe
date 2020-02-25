package com.example.test.testingHMS.user.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.user.model.Previlege;
import com.example.test.testingHMS.user.repository.PrevilegeRepository;
import com.example.test.testingHMS.user.service.PrevilegeService;

@Service
public class PrevilegeServiceImpl implements PrevilegeService
{
	@Autowired
	PrevilegeRepository repo;
	
	public Previlege save(Previlege pw)
	{
		return repo.save(pw);
	}
	
	public Previlege findByName(String name)
	{
		return repo.findByName(name);
	}
	
	

}
