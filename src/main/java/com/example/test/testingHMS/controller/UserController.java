package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.TestingHmsApplication;
import com.example.test.testingHMS.security.JwtTokenProvider;
import com.example.test.testingHMS.security.helper.JwtAuthenticationResponse;
import com.example.test.testingHMS.security.helper.LoginRequest;
import com.example.test.testingHMS.security.helper.PasswordEncodeUtil;
import com.example.test.testingHMS.user.dto.UserDTO;
import com.example.test.testingHMS.user.model.DoctorSpecialization;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.DoctorSpecializationRepository;
import com.example.test.testingHMS.user.repository.UserRepository;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/user")
public class UserController 
{
	public static Logger Logger=LoggerFactory.getLogger(UserController.class);
	
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtTokenProvider tokenProvider;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
    AuthenticationManager authenticationManager;
	
	
	@Autowired
	DoctorSpecializationRepository doctorSpecializationRepository;
	
	
	
	
	@RequestMapping(value="/signin",method=RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        Map<String,Object> c=new HashMap<>();
         User user=getOneUserSecurity(loginRequest.getUsernameOrEmail());
        c.put("token", new JwtAuthenticationResponse(jwt));
        c.put("role",user.getRole());
        c.put("userName",user.getFirstName()+" "+user.getLastName());
        return ResponseEntity.ok(c);
    }
	
	
	/*
	 * for checking encoded password (HASHING)
	 */
	@RequestMapping(value="/getPassword/{id}",method=RequestMethod.GET)
	public boolean getPassword(@PathVariable String id)
	{
		User user=userServiceImpl.findOneByUserId(id);
		return bCryptPasswordEncoder.matches("nikhlilrev", user.getPasswordStuff().getConfirmPassword());
	}
	
	//for security
	@RequestMapping(value="/getOneUser/{id}",method=RequestMethod.GET)
	public User getOneUserSecurity(@PathVariable("id") String id) 
	{
		User user=null;
		user=userServiceImpl.findByUserName(id);
		/*if(user==null)
		{
			user=userServiceImpl.findOneByUserId(id);
			
		}*/
		return user;
	}
	
	
	@RequestMapping(value="/me",method=RequestMethod.GET)
	public ResponseEntity<?> getMe(Principal p) 
	{
		 return ResponseEntity.ok(p.getName());
		
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.POST)
	public ResponseEntity<?> logoutMe() 
	{
		System.out.println(SecurityContextHolder.getContext().getAuthentication().getCredentials());
		SecurityContextHolder.getContext().setAuthentication(null);
		System.out.println(SecurityContextHolder.getContext().getAuthentication());
		 return ResponseEntity.ok("LOGOUT SUCCESSFULL");
		
	}
	


	
	
	// Get All User
	@RequestMapping(value="/getAll",method=RequestMethod.GET)
	public List<Object> getAllUser() 
	{
		List<Object> display=new ArrayList<>();
		 Iterable<User> user=userRepository.findAll();
		 List<DoctorSpecialization> docSpc=doctorSpecializationRepository.findAll();
		 display.add(user);
		 display.add(docSpc);
		return display;
	}
	
	/*
	 * To find the count of doctor,INPATIENT and OUTPATIENT
	 */
	@RequestMapping(value="/getdoctors",method=RequestMethod.GET)
	public Map<String, Long> getDoctorCount(){
		return userServiceImpl.getDoctorCount();
	}
	
	//simple code
	
	
	// GET One User
	@RequestMapping(value="/getOne/{id}",method=RequestMethod.GET)
	public User getOneUser(@PathVariable("id") String id) 
	{
		return userServiceImpl.findOneByUserId(id);
		
	}
	

	// Update User
	@RequestMapping(value="/update/{id}",method=RequestMethod.PUT)
	public void updateUser(@RequestBody UserDTO userdto,@PathVariable String id)
	{	
		
		PasswordEncodeUtil
		.encryptedPasswordStuff(passwordEncoder, userdto.getPasswordStuff());
		User user=new User();
		BeanUtils.copyProperties(userdto, user);
	
		
		userServiceImpl.updateUser(user,id);
	}
	
	//Adding new Specialization
	@RequestMapping(value="/speacialization",method=RequestMethod.POST)
	public void addSpecial(@RequestBody DoctorSpecialization specInfo)
	{
         doctorSpecializationRepository.save(specInfo);
	}
	
	// Get Data for drop down / pageLoading 
	@RequestMapping(value="/create",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public List<Object> getinfo()
	{	
		return userServiceImpl.pageLoad();
	}
	
	

	// Insert the user data 
	@RequestMapping(value="/create",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public void createUser(@RequestBody UserDTO userdto,Principal p) throws Exception
	{
		
		
		userdto.setUserId(userServiceImpl.findFirstByOrderByUserIdDesc());
		PasswordEncodeUtil
		.encryptedPasswordStuff(passwordEncoder, userdto.getPasswordStuff());

		User newuser=new User();
		BeanUtils.copyProperties(userdto, newuser);
		
		String fname=newuser.getFirstName();
		String mname=newuser.getMiddleName();
		String lname=newuser.getLastName();
		long mobile=newuser.getPersonalContactNumber();
		
		if(newuser.getRoleName().equalsIgnoreCase("DOCTOR"))
		{
			fname="Dr. "+newuser.getFirstName();
		}
		
		User userExists = userServiceImpl.findByFirstNameAndMiddleNameAndLastNameAndPersonalContactNumberAndStatus(fname, mname, lname, mobile,"ACTIVE");
		
		if(userExists!=null)
		{
			throw new RuntimeException("User Already Exists !");
		}
		else if(userServiceImpl.findByUserName(newuser.getUserName())!=null)
		{
			throw new RuntimeException("UserName already exists !");
		}

			
		
		if(newuser.getRoleName().equals("DOCTOR"))
		{
			newuser.setFirstName("Dr. "+newuser.getFirstName());
		}
		userServiceImpl.computeUser(newuser,p);

		
	} 
	
	
	/*
	 * For User
	 * update password( get hint question)
	 */
	@RequestMapping(value="/hint/{username}",method=RequestMethod.GET,consumes=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> getQuestions(@PathVariable String username)
	{
			return userServiceImpl.getHint(username);
		
	}
	

	/*
	 * For user
	 * update password( update new password)
	 */
	@RequestMapping(value="/password/{username}",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public void updatePassword(@RequestBody Map<String, String> info,@PathVariable String username)
	{
		userServiceImpl.updatePassword(info, username);
		
	}
	
	
	/*
	 * For admin
	 * update password( update new password)
	 */
	@RequestMapping(value="/admin/password/{userId}",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
	public void adminUpdatePassword(@RequestBody Map<String, String> info,@PathVariable String username)
	{
		userServiceImpl.adminUpdatePassword(info, username);
		
	}
	
	
	// Retrieve the user data
	@RequestMapping(value="/{id}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public User geteUser(@PathVariable String id) 
	{
		return userServiceImpl.findOneByUserId(id);
	
	}
	
	/*
	 * To Inactivate user 
	 */
	@RequestMapping(value="/deactivate/{userId}",method=RequestMethod.PUT)
	public void deactivate(@PathVariable String userId)
	{
		userServiceImpl.deactivate(userId);
	}
	
	/*
	 * To Activate user 
	 */
	@RequestMapping(value="/activate/{userId}",method=RequestMethod.PUT)
	public void activate(@PathVariable String userId)
	{
		userServiceImpl.activate(userId);
	}
	
	
}
