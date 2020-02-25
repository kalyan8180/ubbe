package com.example.test.testingHMS.patient.model;

import java.io.Serializable;
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
@Table(name = "v_patient_type_d")
public class PatientTypes implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="p_type_id")
	private Long pTypeId;
	
	@Column(name="p_type")
	private String pType;
	
	@Column(name="p_subtype")
	private String pSubtype;

	@JsonIgnore
	@OneToMany(mappedBy = "patientType")
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
