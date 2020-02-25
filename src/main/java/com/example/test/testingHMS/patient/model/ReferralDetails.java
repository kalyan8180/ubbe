package com.example.test.testingHMS.patient.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "v_referral_details_d")
public class ReferralDetails implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ref_id")
	private Long refId;
	
	@Column(name="source")
	private String source;
	
	@Column(name="ref_name")
	private String refName;
	
	@Column(name="ref_add")
	private String refAdd;
	
	@Column(name="ref_mail")
	private String refEmail;
	
	@Column(name="ref_phone")
	private long refPhone;

	@JsonIgnore
	@OneToMany(mappedBy = "vRefferalDetails",cascade=CascadeType.ALL)
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
