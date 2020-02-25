package com.example.test.testingHMS.user.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.example.test.testingHMS.user.model.User;


public interface UserService 
{

	public User save(User pw);
	
	public Long count();
	
	User findOneByUserId(String id);
	
	public void computeUser(User user,Principal p);
	
	public String findFirstByOrderByUserIdDesc();
	
	public List<User> findByUserRole(String name);
	
	List<User> findByRole(String role);
	
	User findByFirstNameAndLastName(String fname,String lname);
	
	User findByFirstNameAndMiddleNameAndLastNameAndPersonalContactNumberAndStatus(String fname,String mname,String lname,long mobile,String status);

	List<User> findByRoleId();
	
	Iterable<User> findAll();
	
	User findByUserName(String name);
	
	Map<String, Long> getDoctorCount();
	
	void updateUser(User user,String id);
	
	List<Object> pageLoad();
	
	Map<String, String> getHint(String username);
	
	void updatePassword(Map<String, String> info, String username);
	
	void adminUpdatePassword( Map<String, String> info, String username);

	void deactivate(String userId);
	
	void activate(String userId);

	
}
