package com.example.test.testingHMS.user.service;

import com.example.test.testingHMS.user.model.PasswordStuff;
import com.example.test.testingHMS.user.model.User;


public interface PasswordStuffService 
{
	
	public PasswordStuff save(PasswordStuff pw);
	
	public long count();
	
	public String findFirstByOrderByPasswordIdDesc();

	PasswordStuff findByUser(User id);

}
