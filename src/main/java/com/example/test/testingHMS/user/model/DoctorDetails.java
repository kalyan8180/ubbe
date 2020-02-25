package com.example.test.testingHMS.user.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "v_doctor_details_f")
public class DoctorDetails implements Serializable{

	@Id
	@Column(name = "doctor_id")
	private String doctorId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "middle_name")
	private String middleName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@NotNull
	@Column(name = "specilization")
	private String specilization;
	
	@Column(name = "op_fee")
	private int opFee;

	// Day care
	@Column(name = "ip_dc")
	private int ipDayCare;
	
	
	//Emergency
	@Column(name = "ip_emrgny")
	private int ipEmergency;
	
	
	//general ward female
	@Column(name = "ip_gen_female")
	private int ipGenFemale;
	
	
	//general ward male
	@Column(name = "ip_gen_male")
	private int ipGenMale;
	
	
	// isolation fee
	@Column(name = "ip_isolation")
	private int ipIsolation;
	
	
	// Adult ICU
	@Column(name = "ip_a_icu")
	private int ipAicu;
	
	
	@Column(name = "ip_picu")
	private int ipPicu;
	
	
	@Column(name = "ip_nicu")
	private int ipNicu;
	
	//single sharing
	@Column(name = "ip_ss")
	private int ipSs;
	
	//Double sharing
	@Column(name = "ip_ds")
	private int ipDs;
	
	
	@Column(name = "qualification")
	private String qualification;
	
	@Column(name = "dr_registration_no")
	private String drRegistrationo;
	
	@Column(name = "created_date")
	private Timestamp createdDate;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "modified_date")
	private Timestamp modifiedDate;
	
	@Column(name = "modified_by")
	private String modifiedBy;

	@JsonBackReference
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User doctorUser;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSpecilization() {
		return specilization;
	}

	public void setSpecilization(String specilization) {
		this.specilization = specilization;
	}

	public String getDrRegistrationo() {
		return drRegistrationo;
	}

	public void setDrRegistrationo(String drRegistrationo) {
		this.drRegistrationo = drRegistrationo;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public User getDoctorUser() {
		return doctorUser;
	}

	public void setDoctorUser(User doctorUser) {
		this.doctorUser = doctorUser;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public int getOpFee() {
		return opFee;
	}

	public void setOpFee(int opFee) {
		this.opFee = opFee;
	}

	public int getIpDayCare() {
		return ipDayCare;
	}

	public void setIpDayCare(int ipDayCare) {
		this.ipDayCare = ipDayCare;
	}

	public int getIpEmergency() {
		return ipEmergency;
	}

	public void setIpEmergency(int ipEmergency) {
		this.ipEmergency = ipEmergency;
	}

	public int getIpGenFemale() {
		return ipGenFemale;
	}

	public void setIpGenFemale(int ipGenFemale) {
		this.ipGenFemale = ipGenFemale;
	}

	public int getIpGenMale() {
		return ipGenMale;
	}

	public void setIpGenMale(int ipGenMale) {
		this.ipGenMale = ipGenMale;
	}

	public int getIpIsolation() {
		return ipIsolation;
	}

	public void setIpIsolation(int ipIsolation) {
		this.ipIsolation = ipIsolation;
	}

	public int getIpAicu() {
		return ipAicu;
	}

	public void setIpAicu(int ipAicu) {
		this.ipAicu = ipAicu;
	}

	public int getIpPicu() {
		return ipPicu;
	}

	public void setIpPicu(int ipPicu) {
		this.ipPicu = ipPicu;
	}

	public int getIpNicu() {
		return ipNicu;
	}

	public void setIpNicu(int ipNicu) {
		this.ipNicu = ipNicu;
	}

	public int getIpSs() {
		return ipSs;
	}

	public void setIpSs(int ipSs) {
		this.ipSs = ipSs;
	}

	public int getIpDs() {
		return ipDs;
	}

	public void setIpDs(int ipDs) {
		this.ipDs = ipDs;
	}


	

}