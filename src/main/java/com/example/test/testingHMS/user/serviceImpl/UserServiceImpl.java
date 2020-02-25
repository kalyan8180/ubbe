package com.example.test.testingHMS.user.serviceImpl;

import java.security.Principal;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.patient.idGenerator.UserIdGenerator;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.serviceImpl.PatientRegistrationServiceImpl;
import com.example.test.testingHMS.pharmacist.model.Vendors;
import com.example.test.testingHMS.pharmacist.serviceImpl.MedicineProcurementServiceImpl;
import com.example.test.testingHMS.pharmacist.serviceImpl.VendorsServiceImpl;
import com.example.test.testingHMS.security.helper.PasswordEncodeUtil;
import com.example.test.testingHMS.user.model.DoctorDetails;
import com.example.test.testingHMS.user.model.DoctorSpecialization;
import com.example.test.testingHMS.user.model.PasswordStuff;
import com.example.test.testingHMS.user.model.Role;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.UserRepository;
import com.example.test.testingHMS.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger Logger=LoggerFactory.getLogger(UserServiceImpl.class);
	
	
	@Autowired
	UserRepository repo;

	@Autowired
	PasswordStuffServiceImpl passwordStuffServiceImpl;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
		
	@Autowired
	DoctorSpecializationServiceImpl doctorSpecializationServiceImpl;

	@Autowired
	UserIdGenerator userIdGenerator;
	
	
	@Autowired
	DoctorDetailsServiceImpl doctorDetailsServiceImpl;
	
	@Autowired
	PatientRegistrationServiceImpl patientRegistrationServiceImpl;

	@Autowired
	PrevilegeServiceImpl previlegeServiceImpl;

	@Autowired
	RolePrivilegationMappingServiceImpl rolePrivilegationMappingServiceImpl;

	@Autowired
	RoleServiceImpl roleServiceImpl;

	
	
	@Autowired
	VendorsServiceImpl vendorsServiceImpl;

	public User save(User pw) {
		return repo.save(pw);
	}

	public Long count() {
		return repo.count();
	}

	public User findOneByUserId(String id) {
		return repo.findOneByUserId(id);
	}

	public String findFirstByOrderByUserIdDesc() {
		User userLast = repo.findFirstByOrderByUserIdDesc();
		System.out.println("User last value" + userLast);
		String userNextId = null;
		if (userLast == null) {
			userNextId = "UB00001";
		} else {
			String userLastId = userLast.getUserId();
			int userIntId = Integer.parseInt(userLastId.substring(2));
			userIntId += 1;
			userNextId = "UB" + String.format("%05d", userIntId);
		}
		return userNextId;

	}

	public void computeUser(User user,Principal principal) 
	{
		User userSecurity=null;
		String createdById=null;
		if(principal!=null)
		{
		userSecurity=findByUserName(principal.getName());
		createdById=userSecurity.getUserId();
		}
		if(user.getUserName().length()<6)
		{
			throw new RuntimeException("UserName should be greater than 6 characters");
		}
		
		
		
		String passwordStuffNext = passwordStuffServiceImpl.findFirstByOrderByPasswordIdDesc();

		Role role = roleServiceImpl.findByRoleName(user.getRoleName());
		
		user.setRole(role.getRoleName());
		user.setUserRole(role);
		user.setTimeZone("IST");
		user.setStatus("ACTIVE");
		
		List<Map<String,String>> docSpec=user.getDoctorSpecialization();
		
		

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		user.setCreatedAt(timestamp);

		PasswordStuff nextPasswordStuff = user.getPasswordStuff();
		nextPasswordStuff.setUser(user);
		nextPasswordStuff.setPasswordId(passwordStuffNext);

		DoctorDetails doctorDetails= user.getDoctorDetails();
		doctorDetails.setDoctorId(doctorDetailsServiceImpl.getNextId());
		doctorDetails.setDoctorUser(user);
		doctorDetails.setFirstName(user.getFirstName());
		if(user.getMiddleName()!=null)
		{
				doctorDetails.setMiddleName(user.getMiddleName());
		}
		doctorDetails.setLastName(user.getLastName());
		doctorDetails.setCreatedBy(createdById);
		doctorDetails.setQualification(user.getDoctorDetails().getQualification());
		doctorDetails.setDrRegistrationo(user.getUserId());
		doctorDetails.setCreatedDate(timestamp);
		if(user.getRoleName().equals("DOCTOR"))
		{
			if(user.getDoctorDetails().getSpecilization()==null) {
				
				throw new RuntimeException("Please select the Specilization");
			}
			
			doctorDetails.setSpecilization(user.getDoctorDetails().getSpecilization());
		}
		else
		{
			doctorDetails.setSpecilization("Not Applicable");
		}

					
		save(user);
		
		

	}
	
	public User findByUserName(String name)
	{
		return repo.findByUserName(name);
	}

	@Override
	public List<User> findByUserRole(String name) 
	{
		
		return repo.findByRole(name);
	}
	
	public User findByFirstNameAndLastName(String fname,String lname)
	{
		
		return repo.findByFirstNameAndLastName(fname, lname);
	}
	
	public Iterable<User> findAll()
	{
		return repo.findAll();
	}
	
	public List<User> findByRole(String role) {
				return repo.findByRole(role);
	}
	
	public Map<String, Long> getDoctorCount()
	{
		Map<String, Long> map=new HashMap<>();
		 
		 List<User> user=findByRole("DOCTOR");
		 long count=user.size();
		 map.put("doctorcount", count);
		 
			
		 	List<PatientRegistration> count2=patientRegistrationServiceImpl.findByPType("INPATIENT");
			
			long patientCount=count2.size();
			
			map.put("INPATIENT", patientCount);
			
			List<PatientRegistration> count3=patientRegistrationServiceImpl.findByPType("OUTPATIENT");
			
			long patientOutCount=count3.size();
			
			map.put("OUTPATIENT", patientOutCount);
			
			List<Vendors> vendors=vendorsServiceImpl.findAll();
			
			long vendorsCount=vendors.size();
			
			map.put("vendors", vendorsCount);
			
			
			return map;
	}

	public 	void updateUser(User user,String id)
	{
		Role role = roleServiceImpl.findByRoleName(user.getRoleName());
		user.setUserId(id);
		user.setRole(role.getRoleName());
		user.setUserRole(role);

		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
		user.setCreatedAt(timestamp);
		
		User userInfo=findOneByUserId(id);
		user.setUserId(userInfo.getUserId());
		user.setTimeZone("IST");
		user.setStatus("ACTIVE");
		
		PasswordStuff passwordStuffInfo=userInfo.getPasswordStuff();
		
		PasswordStuff nextPasswordStuff = user.getPasswordStuff();
		nextPasswordStuff.setUser(user);
		nextPasswordStuff.setPasswordId(passwordStuffInfo.getPasswordId());
		
		DoctorDetails doctorDetailsInfo=userInfo.getDoctorDetails();
		DoctorDetails doctorDetails= user.getDoctorDetails();
		
		doctorDetails.setDoctorId(doctorDetailsInfo.getDoctorId());
		doctorDetails.setDoctorUser(user);
		if(userInfo.getUserRole().getRoleName().equalsIgnoreCase("DOCTOR"))
		{
			doctorDetails.setFirstName(user.getFirstName());
		} 
		if(user.getMiddleName()!=null)
		{
			doctorDetails.setMiddleName(user.getMiddleName());
		}
		doctorDetails.setQualification(user.getDoctorDetails().getQualification());
		doctorDetails.setLastName(user.getLastName());
		doctorDetails.setDrRegistrationo(user.getUserId());
		doctorDetails.setCreatedDate(timestamp);

		if(userInfo.getUserRole().getRoleName().equalsIgnoreCase("DOCTOR"))
		{
			doctorDetails.setSpecilization(user.getDoctorDetails().getSpecilization());
		}
		else
		{
			doctorDetails.setSpecilization("NOT APPLICABLE");
		}

		
		repo.save(user);
		
	}
	
	public 	List<Object> pageLoad()
	{
		List<Object> a1=new ArrayList<>();
		Iterable<Role> role=roleServiceImpl.findAll();
		Iterable<User> user=findAll();
		List<DoctorSpecialization> docSpec=doctorSpecializationServiceImpl.findAll();
		a1.add(role);
		a1.add(user);
		a1.add(docSpec);
		userIdGenerator=new UserIdGenerator(findFirstByOrderByUserIdDesc());
		a1.add(userIdGenerator);
		
		return a1;
	}
	
	public Map<String, String> getHint(String username)
	{
		User user=findByUserName(username);
		Map<String,String> info=new HashMap<String,String>();
		if(user!=null)
		{
			PasswordStuff passwordStuff=user.getPasswordStuff();
			info.put("HINT1", passwordStuff.getHintQuestion1());
			info.put("HINT2", passwordStuff.getHintQuestion2());
			return info;
		}
		else
		{
			return null;
		}
		
	}
	
	public void updatePassword(Map<String, String> info, String username)
	{
		User user=findByUserName(username);
		PasswordStuff passwordStuff=user.getPasswordStuff();
		if(passwordStuff.getHintAnswer1().equals(info.get("HINT1")) && passwordStuff.getHintAnswer2().equals(info.get("HINT2")))
		{
			passwordStuff.setPassword(info.get("ANS1"));
			passwordStuff.setConfirmPassword(info.get("ANS2"));
			PasswordEncodeUtil
			.encryptedPasswordStuff(passwordEncoder, passwordStuff);
			passwordStuffServiceImpl.save(passwordStuff);
		}
	}
	
	public void adminUpdatePassword( Map<String, String> info, String username)
	{
		User user=findOneByUserId(username);
		PasswordStuff passwordStuff=user.getPasswordStuff();
		passwordStuff.setPassword(info.get("password"));
		passwordStuff.setConfirmPassword(info.get("confirm"));
		PasswordEncodeUtil
		.encryptedPasswordStuff(passwordEncoder, passwordStuff);
		passwordStuffServiceImpl.save(passwordStuff);
	}
	
	public 	void deactivate(String userId)
	{
		User user=findOneByUserId(userId);
		user.setStatus("INACTIVE");
		save(user);
	}
	
	public void activate(String userId)
	{
		User user=findOneByUserId(userId);
		user.setStatus("ACTIVE");
		save(user);
	}
	
	public User findByFirstNameAndMiddleNameAndLastNameAndPersonalContactNumberAndStatus(String fname,String mname,String lname,long mobile,String status)
	{
		return repo.findByFirstNameAndMiddleNameAndLastNameAndPersonalContactNumberAndStatus(fname, mname, lname,mobile,status);
	}
	
	public List<User> findByStatus(String status)
	{
		return repo.findByStatus(status);
	}

    public List<User> findByRoleId() 
    {
		return repo.findByRoleId();
	}

	
}
