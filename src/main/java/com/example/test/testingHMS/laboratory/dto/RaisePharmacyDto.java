package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.test.testingHMS.laboratory.model.NurseLaboratory;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

public class RaisePharmacyDto {
	private String nurseRaiseId;

	private String location;
	
	private long quantity;
	
	private Timestamp date;
	
	private String raisedBy;
	
	private User userRaisePharmacy;

	private PatientRegistration patientRegistrationRaisePharmacy;

	private List<NurseLaboratory> nurseLaboratory;

	public String getNurseRaiseId() {
		return nurseRaiseId;
	}

	public void setNurseRaiseId(String nurseRaiseId) {
		this.nurseRaiseId = nurseRaiseId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(String raisedBy) {
		this.raisedBy = raisedBy;
	}

	public User getUserRaisePharmacy() {
		return userRaisePharmacy;
	}

	public void setUserRaisePharmacy(User userRaisePharmacy) {
		this.userRaisePharmacy = userRaisePharmacy;
	}

	public PatientRegistration getPatientRegistrationRaisePharmacy() {
		return patientRegistrationRaisePharmacy;
	}

	public void setPatientRegistrationRaisePharmacy(PatientRegistration patientRegistrationRaisePharmacy) {
		this.patientRegistrationRaisePharmacy = patientRegistrationRaisePharmacy;
	}

	public List<NurseLaboratory> getNurseLaboratory() {
		return nurseLaboratory;
	}

	public void setNurseLaboratory(List<NurseLaboratory> nurseLaboratory) {
		this.nurseLaboratory = nurseLaboratory;
	}
	
	
}