package com.example.test.testingHMS.user.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_doctor_specilzation_d")
public class DoctorSpecialization {
	
	
	@Id
	@Column(name="specilization_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long specilizationId;
	
	@Column(name="spec_name")
	private String specName;
	
	

	public long getSpecilizationId() {
		return specilizationId;
	}


	public void setSpecilizationId(long specilizationId) {
		this.specilizationId = specilizationId;
	}


	public String getSpecName() {
		return specName;
	}


	public void setSpecName(String specName) {
		this.specName = specName;
	}


	

	
	
	




}