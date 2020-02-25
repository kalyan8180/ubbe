package com.example.test.testingHMS.user.service;

import java.util.List;

import com.example.test.testingHMS.user.model.Role;


public interface RoleService 
{

	public Role save(Role pw);
	
	public Role findByRoleName(String name);
	
	public Iterable<Role> findAll();
	

}
