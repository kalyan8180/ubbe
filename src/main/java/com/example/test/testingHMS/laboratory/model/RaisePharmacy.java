package com.example.test.testingHMS.laboratory.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name = "v_raise_pharmacy_f")
public class RaisePharmacy {
	@Id
	@Column(name = "nurse_raise_id")
	private String nurseRaiseId;

	@Column(name = "location")
	private String location;
	
	@Column(name = "quantity")
	private long quantity;
	
	@Column(name = "date")
	private Timestamp date;
	
	@Column(name = "raised_by")
	private String raisedBy;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User userRaisePharmacy;

	@ManyToOne
	@JoinColumn(name = "p_reg_id")
	private PatientRegistration patientRegistrationRaisePharmacy;

	@OneToMany(mappedBy = "raisePharmacyNurse", cascade = CascadeType.ALL)
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