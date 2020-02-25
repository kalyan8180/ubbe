package com.example.test.testingHMS.user.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.user.model.Role;
import com.example.test.testingHMS.user.model.RolePrivilegationMapping;

@Repository
public interface RolePrivilegationMappingRepository extends CrudRepository<RolePrivilegationMapping,Long>{
	
	public List<RolePrivilegationMapping> findByRole(List<Role> id);
	
}
