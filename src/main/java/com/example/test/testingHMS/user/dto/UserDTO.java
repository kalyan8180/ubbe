package com.example.test.testingHMS.user.dto;

import java.sql.Timestamp
;
import java.util.List;
import java.util.Map;

import com.example.test.testingHMS.ambulance.model.AmbulancePatientDetails;
import com.example.test.testingHMS.ambulance.model.AmbulanceServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.MasterCheckupService;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Vendors;
import com.example.test.testingHMS.pharmacist.model.VendorsInvoice;
import com.example.test.testingHMS.user.model.DoctorDetails;
import com.example.test.testingHMS.user.model.PasswordStuff;
import com.example.test.testingHMS.user.model.Role;


public class UserDTO {

	private String userId;
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;
	
	private long personalContactNumber;
	
	private long workContactNumber;
	
	private String userName;
	
	private String pin;
	
	private String department;
	
	private String email;
	
	private String status;
	
	private String timeZone;
	
	private Timestamp deletedAt;
	
	private Timestamp createdAt;
	
	private String refName;
	
	private String location;
	
	private String description;
	
	private String role;
	
	private transient String roleName;
	
	private transient String uId;
	
	private transient List<Map<String,String>> doctorSpecialization;
	
	private DoctorDetails doctorDetails;

	private PasswordStuff passwordStuff;
	
	private List<PatientRegistration> patientRegistrations;

	private List<Vendors> vendors ;
	
	
	private List<LaboratoryRegistration>  laboratoryRegistration;
	
	private List<VendorsInvoice> vendorsInvoice ;
	
	private List<AmbulanceServices> ambulanceServices;
	
	private List<AmbulancePatientDetails> ambulancePatientDetails;

	private Role userRole;
	
	
	private List<MasterCheckupService> masterCheckupService;
	


	public PasswordStuff getPasswordStuff() {
		return passwordStuff;
	}

	public void setPasswordStuff(PasswordStuff passwordStuff) {
		this.passwordStuff = passwordStuff;
	}

	
	

	

	public Role getUserRole() {
		return userRole;
	}


	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	

	
	public long getPersonalContactNumber() {
		return personalContactNumber;
	}


	public void setPersonalContactNumber(long personalContactNumber) {
		this.personalContactNumber = personalContactNumber;
	}


	public long getWorkContactNumber() {
		return workContactNumber;
	}


	public void setWorkContactNumber(long workContactNumber) {
		this.workContactNumber = workContactNumber;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}


	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}


	public String getMiddleName() {
		return middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public List<PatientRegistration> getPatientRegistrations() {
		return patientRegistrations;
	}


	public void setPatientRegistrations(List<PatientRegistration> patientRegistrations) {
		this.patientRegistrations = patientRegistrations;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}
	
	


	public List<Vendors> getVendors() {
		return vendors;
	}


	public void setVendors(List<Vendors> vendors) {
		this.vendors = vendors;
	}
	
	


	public List<VendorsInvoice> getVendorsInvoice() {
		return vendorsInvoice;
	}


	public void setVendorsInvoice(List<VendorsInvoice> vendorsInvoice) {
		this.vendorsInvoice = vendorsInvoice;
	}

	public List<LaboratoryRegistration> getLaboratoryRegistration() {
		return laboratoryRegistration;
	}


	public void setLaboratoryRegistration(List<LaboratoryRegistration> laboratoryRegistration) {
		this.laboratoryRegistration = laboratoryRegistration;
	}


	public DoctorDetails getDoctorDetails() {
		return doctorDetails;
	}


	public void setDoctorDetails(DoctorDetails doctorDetails) {
		this.doctorDetails = doctorDetails;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<AmbulanceServices> getAmbulanceServices() {
		return ambulanceServices;
	}

	public void setAmbulanceServices(List<AmbulanceServices> ambulanceServices) {
		this.ambulanceServices = ambulanceServices;
	}

	public List<AmbulancePatientDetails> getAmbulancePatientDetails() {
		return ambulancePatientDetails;
	}

	public void setAmbulancePatientDetails(List<AmbulancePatientDetails> ambulancePatientDetails) {
		this.ambulancePatientDetails = ambulancePatientDetails;
	}

	public List<Map<String, String>> getDoctorSpecialization() {
		return doctorSpecialization;
	}

	public void setDoctorSpecialization(List<Map<String, String>> doctorSpecialization) {
		this.doctorSpecialization = doctorSpecialization;
	}

	

	public List<MasterCheckupService> getMasterCheckupService() {
		return masterCheckupService;
	}

	public void setMasterCheckupService(List<MasterCheckupService> masterCheckupService) {
		this.masterCheckupService = masterCheckupService;
	}


	
	
	
	

	
	
	
	

}
