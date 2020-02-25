 package com.example.test.testingHMS.patient.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "v_patient_details_d")
public class PatientDetails implements Serializable{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private Long patientId;
	
	@Column(name="umr")
	private String umr;

	@Column(name="title")
	private String title;
	
	@Column(name="deleted_at")
	private Timestamp deletedAt;
	
	@Column(name="deleted")
	private String deleted;
	
	@Column(name="first_name")
	private String firstName;

	@Column(name="middle_name")
	private String middleName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="dob")
	private Timestamp dob;
	
	@Column(name="responsible_person_name")
	private String responsiblePersonName;
	
	@Column(name="father_name")
	private String motherName;
	
	@Column(name="discharged")
	private String discharged;
	
	@Column(name="blood_group")
	private String bloodGroup;
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="nationality")
	private String nationality;
	
	@Column(name="religion")
	private String religion;
	
	@Column(name="hospital_location")
	private String hospitalLocation;
	
	@Column(name="alias_name")
	private String aliasName;
	
	@Column(name="mobile")
	private long mobile;
	
	@Column(name="telephone")
	private long telephone;
	
	@Column(name="email")
	private String email;
	
	@Column(name="mode_of_communication")
	private String modeOfCommunication;
	
	@Column(name="address")
	private String address;
	
	@Column(name="age")
	private String age;
	
	@Column(name="area")
	private String area;
	
	@Column(name="consultant")
	private String consultant;
	
	@Column(name="occupation")
	private String occupation;
	
	@Column(name="maritial_status")
	private String maritialStatus;
	
	
	@Column(name="responsible_person")
	private String responsiblePerson;
	
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="country")
	private String country;
	
	@Column(name="pin")
	private String pin;
	
	@Column(name="passport_no")
	private String passportNo;
	
	@Column(name="issue_date")
	private Timestamp issueDate;
	
	@Column(name="issued_at")
	private String issuedAt;
	
	@Column(name="expiry_date")
	private Timestamp expiryDate;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="company_code")
	private String companyCode;
	
	@Column(name="company_fee")
	private long companyFee;
	
	
	private transient int ageCalculation;
	
	private transient String patientTypeName;
	
	private transient String marketingName;
	
	private transient Map<String,String> refName;
	
	private transient ReferralDetails refDetails;

	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "ref_id")
	private ReferralDetails vRefferalDetails;

	@OneToMany(mappedBy = "patientDetails",cascade=CascadeType.ALL)
	private Set<PatientRegistration> vPatientRegistration;
	


	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "q_id")
	private MarketingQuestions vMarketingQuestion;

	public String getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(String responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
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
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
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

	

	public String getHospitalLocation() {
		return hospitalLocation;
	}

	public void setHospitalLocation(String hospitalLocation) {
		this.hospitalLocation = hospitalLocation;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
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

	public ReferralDetails getRefDetails() {
		return refDetails;
	}

	public void setRefDetails(ReferralDetails refDetails) {
		this.refDetails = refDetails;
	}

	

}
