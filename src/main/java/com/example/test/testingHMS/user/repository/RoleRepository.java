package com.example.test.testingHMS.user.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.user.model.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role,Long>{

	Role findByRoleName(String name);
	
	Iterable<Role> findAll();
	
}
