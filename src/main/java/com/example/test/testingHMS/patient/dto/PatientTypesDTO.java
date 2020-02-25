package com.example.test.testingHMS.patient.dto;

import java.util.List;

import com.example.test.testingHMS.patient.model.PatientRegistration;



public class PatientTypesDTO {

	private Long pTypeId;
	
	private String pType;
	
	private String pSubtype;

	private List<PatientRegistration> vPatientRegistration;

	public Long getpTypeId() {
		return pTypeId;
	}

	public void setpTypeId(Long pTypeId) {
		this.pTypeId = pTypeId;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public String getpSubtype() {
		return pSubtype;
	}

	public void setpSubtype(String pSubtype) {
		this.pSubtype = pSubtype;
	}

	public List<PatientRegistration> getvPatientRegistration() {
		return vPatientRegistration;
	}

	public void setvPatientRegistration(List<PatientRegistration> vPatientRegistration) {
		this.vPatientRegistration = vPatientRegistration;
	}


	
	
	
}
