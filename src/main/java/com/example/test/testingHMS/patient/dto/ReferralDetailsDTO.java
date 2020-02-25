package com.example.test.testingHMS.patient.dto;

import java.util.Set;

import com.example.test.testingHMS.patient.model.PatientDetails;



public class ReferralDetailsDTO {

	private Long refId;
	
	private String source;
	
	private String refName;
	
	private String refAdd;
	
	private long refPhone;
	
	private String refEmail;

	private Set<PatientDetails> vPatientDetails;

	public Long getRefId() {
		return refId;
	}

	public void setRefId(Long refId) {
		this.refId = refId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}

	public String getRefAdd() {
		return refAdd;
	}

	public void setRefAdd(String refAdd) {
		this.refAdd = refAdd;
	}

	

	public long getRefPhone() {
		return refPhone;
	}

	public void setRefPhone(long refPhone) {
		this.refPhone = refPhone;
	}

	public Set<PatientDetails> getvPatientDetails() {
		return vPatientDetails;
	}

	public void setvPatientDetails(Set<PatientDetails> vPatientDetails) {
		this.vPatientDetails = vPatientDetails;
	}

	public String getRefEmail() {
		return refEmail;
	}

	public void setRefEmail(String refEmail) {
		this.refEmail = refEmail;
	}
	
	
	


}
