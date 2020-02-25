package com.example.test.testingHMS.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test.testingHMS.user.model.Role;
import com.example.test.testingHMS.user.model.User;

@Repository
public interface UserRepository extends CrudRepository<User,String>{

	User findOneByUserId(String id);
	
	User save(User s);
	
	User findFirstByOrderByUserIdDesc();
	
	List<User> findByUserRole(Role role);
	
	@Query(value="select * from second.v_user_d where role_name=:role and status='ACTIVE'",nativeQuery=true)
	List<User> findByRole(@Param("role") String role);
	
	Iterable<User> findAll();
	
	List<User> findByStatus(String status);
	
	User findByFirstNameAndLastName(String fname,String lname);
	
	@Query(value="select * from second.v_user_d where role_name!='DOCTOR' and status='ACTIVE'",nativeQuery=true)
	List<User> findByRoleId();

	User findByFirstNameAndMiddleNameAndLastNameAndPersonalContactNumberAndStatus(String fname,String mname,String lname,long mobile,String status);


	@Query(value="SELECT * FROM second.v_user_d where user_name=:name and status='ACTIVE'",nativeQuery=true)
	User findByUserName(@Param("name") String name);
	
}
