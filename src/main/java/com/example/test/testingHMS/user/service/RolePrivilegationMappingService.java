package com.example.test.testingHMS.user.service;

import java.util.List;

import com.example.test.testingHMS.user.model.Role;
import com.example.test.testingHMS.user.model.RolePrivilegationMapping;


public interface RolePrivilegationMappingService 
{
	public RolePrivilegationMapping save(RolePrivilegationMapping pw);
	public List<RolePrivilegationMapping> findByRole(List<Role> id);
	
	
	
}
