package com.example.test.testingHMS.patient.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import com.example.test.testingHMS.nurse.model.PrescriptionDetails;
import com.example.test.testingHMS.patient.model.MarketingQuestions;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.patient.model.ReferralDetails;



public class PatientDetailsDTO {
	private Long p_id;
	
	private String umr;
	
	private String title;
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;
	
	private Timestamp deletedAt;

	private Timestamp triggeredDate;
	
	private String deleted;
	
	private String discharged;
	
	private String hospitalLocation;
	
	private Timestamp dob;
	
	private String motherName;
	
	private String responsiblePersonName;
	
	private String bloodGroup;
	
	private String gender;
	
	private String Occupation;
	
	private String nationality;
	
	private String religion;
	
	private String aliasName;
	
	private String consultant;
	
	private long mobile;
	
	private long telephone;
	
	private String email;
	
	private String modeOfCommunication;
	
	private String address;
	
	private String area;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String pin;
	
	private String maritialStatus;
	
	private String responsiblePerson;
	
	private String passportNo;
	
	private Timestamp issueDate;
	
	private String issuedAt;
	
	private Timestamp expiryDate;
	
	private String companyName;
	
	private String companyCode;
	
	private long companyFee;
	
	
	private transient String patientTypeName;
	
	private transient int ageCalculation;
	
	private transient String marketingName;
	
	private transient Map<String,String> refName;
	
	private transient ReferralDetails refDetails;

	private ReferralDetails vRefferalDetails;

	private Set<PatientRegistration> vPatientRegistration;

	private MarketingQuestions vMarketingQuestion;
	
	private List<PrescriptionDetails> prescriptionDetails;
	
	public String getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(String responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public Long getP_id() {
		return p_id;
	}

	public void setP_id(Long p_id) {
		this.p_id = p_id;
	}

	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
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

	
	

	public Timestamp getDob() {
		return dob;
	}

	public void setDob(Timestamp dob) {
		this.dob = dob;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
		this.mobile = mobile;
	}

	public long getTelephone() {
		return telephone;
	}

	public void setTelephone(long telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getModeOfCommunication() {
		return modeOfCommunication;
	}

	public void setModeOfCommunication(String modeOfCommunication) {
		this.modeOfCommunication = modeOfCommunication;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public ReferralDetails getvRefferalDetails() {
		return vRefferalDetails;
	}

	public void setvRefferalDetails(ReferralDetails vRefferalDetails) {
		this.vRefferalDetails = vRefferalDetails;
	}

	public Set<PatientRegistration> getvPatientRegistration() {
		return vPatientRegistration;
	}

	public void setvPatientRegistration(Set<PatientRegistration> vPatientRegistration) {
		this.vPatientRegistration = vPatientRegistration;
	}

	public MarketingQuestions getvMarketingQuestion() {
		return vMarketingQuestion;
	}

	public void setvMarketingQuestion(MarketingQuestions vMarketingQuestion) {
		this.vMarketingQuestion = vMarketingQuestion;
	}

	public String getPatientTypeName() {
		return patientTypeName;
	}

	public void setPatientTypeName(String patientTypeName) {
		this.patientTypeName = patientTypeName;
	}

	public String getMarketingName() {
		return marketingName;
	}

	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}

	public String getConsultant() {
		return consultant;
	}

	public void setConsultant(String consultant) {
		this.consultant = consultant;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOccupation() {
		return Occupation;
	}

	public void setOccupation(String occupation) {
		Occupation = occupation;
	}

	public String getMaritialStatus() {
		return maritialStatus;
	}

	public void setMaritialStatus(String maritialStatus) {
		this.maritialStatus = maritialStatus;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public Timestamp getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Timestamp issueDate) {
		this.issueDate = issueDate;
	}

	public String getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Timestamp getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	

	public long getCompanyFee() {
		return companyFee;
	}

	public void setCompanyFee(long companyFee) {
		this.companyFee = companyFee;
	}

	public List<PrescriptionDetails> getPrescriptionDetails() {
		return prescriptionDetails;
	}

	public void setPrescriptionDetails(List<PrescriptionDetails> prescriptionDetails) {
		this.prescriptionDetails = prescriptionDetails;
	}

	public String getHospitalLocation() {
		return hospitalLocation;
	}

	public void setHospitalLocation(String hospitalLocation) {
		this.hospitalLocation = hospitalLocation;
	}

	public String getResponsiblePersonName() {
		return responsiblePersonName;
	}

	public void setResponsiblePersonName(String responsiblePersonName) {
		this.responsiblePersonName = responsiblePersonName;
	}

	public String getDischarged() {
		return discharged;
	}

	public void setDischarged(String discharged) {
		this.discharged = discharged;
	}

	public Map<String, String> getRefName() {
		return refName;
	}

	public void setRefName(Map<String, String> refName) {
		this.refName = refName;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public int getAgeCalculation() {
		return ageCalculation;
	}

	public void setAgeCalculation(int ageCalculation) {
		this.ageCalculation = ageCalculation;
	}

	public Timestamp getTriggeredDate() {
		return triggeredDate;
	}

	public void setTriggeredDate(Timestamp triggeredDate) {
		this.triggeredDate = triggeredDate;
	}

	public ReferralDetails getRefDetails() {
		return refDetails;
	}

	public void setRefDetails(ReferralDetails refDetails) {
		this.refDetails = refDetails;
	}
	
	

	
	
}
