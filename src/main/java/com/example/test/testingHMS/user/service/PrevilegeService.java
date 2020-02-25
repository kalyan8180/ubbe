package com.example.test.testingHMS.user.service;

import com.example.test.testingHMS.user.model.Previlege;

public interface PrevilegeService 
{

	public Previlege save(Previlege pw);
	
	public Previlege findByName(String name);

}
