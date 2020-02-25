package com.example.test.testingHMS.user.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.user.model.Role;
import com.example.test.testingHMS.user.model.RolePrivilegationMapping;
import com.example.test.testingHMS.user.repository.RolePrivilegationMappingRepository;
import com.example.test.testingHMS.user.service.RolePrivilegationMappingService;

@Service
public class RolePrivilegationMappingServiceImpl implements RolePrivilegationMappingService
{
	@Autowired
	RolePrivilegationMappingRepository repo;
	
	public RolePrivilegationMapping save(RolePrivilegationMapping pw)
	{
		return repo.save(pw);
	}

	

	@Override
	public List<RolePrivilegationMapping> findByRole(List<Role> id) {
		
		return repo.findByRole(id);
	}

}
