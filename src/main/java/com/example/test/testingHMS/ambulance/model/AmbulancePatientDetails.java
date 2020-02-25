package com.example.test.testingHMS.ambulance.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name = "v_ambulance_patientdetails_f")
public class AmbulancePatientDetails {

	@Id
	@Column(name = "pat_ambulance_id")
	private String patAmbulanceId;

	@Column(name = "pat_name")
	private String patName;

	@Column(name = "from_location")
	private String fromLocation;

	@Column(name = "to_location")
	private String toLocation;

	@Column(name = "mobile_no")
	private long mobileNo;

	@Column(name = "bill_amount")
	private float billAmount;

	@Column(name = "from_time")
	private Timestamp fromTime;

	@Column(name = "to_time")
	private Timestamp toTime;

	@Column(name = "ambulance_no")
	private String ambulanceNo;

	@Column(name = "driver_name")
	private String driverName;

	@Column(name = "reg_no")
	private String regNo;

	@Column(name = "paid_to")
	private String paidTo;

	@Column(name = "amount_status")
	private String amountStatus;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User ambulancePatientUser;

	@ManyToOne
	@JoinColumn(name = "ambulance_id")
	private AmbulanceServices ambulanceServices;

	@ManyToMany(mappedBy = "ambulancePatientDetail", cascade = CascadeType.ALL)

	private List<PatientRegistration> ambPatientRegistration;

	public String getAmountStatus() {
		return amountStatus;
	}

	public Timestamp getToTime() {
		return toTime;
	}

	public void setToTime(Timestamp toTime) {
		this.toTime = toTime;
	}

	public void setAmountStatus(String amountStatus) {
		this.amountStatus = amountStatus;
	}

	public String getPatAmbulanceId() {
		return patAmbulanceId;
	}

	public void setPatAmbulanceId(String patAmbulanceId) {
		this.patAmbulanceId = patAmbulanceId;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getPaidTo() {
		return paidTo;
	}

	public void setPaidTo(String paidTo) {
		this.paidTo = paidTo;
	}

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public float getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(float billAmount) {
		this.billAmount = billAmount;
	}

	public Timestamp getFromTime() {
		return fromTime;
	}

	public void setFromTime(Timestamp fromTime) {
		this.fromTime = fromTime;
	}

	public String getAmbulanceNo() {
		return ambulanceNo;
	}

	public void setAmbulanceNo(String ambulanceNo) {
		this.ambulanceNo = ambulanceNo;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public User getAmbulancePatientUser() {
		return ambulancePatientUser;
	}

	public void setAmbulancePatientUser(User ambulancePatientUser) {
		this.ambulancePatientUser = ambulancePatientUser;
	}

	public AmbulanceServices getAmbulanceServices() {
		return ambulanceServices;
	}

	public void setAmbulanceServices(AmbulanceServices ambulanceServices) {
		this.ambulanceServices = ambulanceServices;
	}

	public List<PatientRegistration> getAmbPatientRegistration() {
		return ambPatientRegistration;
	}

	public void setAmbPatientRegistration(List<PatientRegistration> ambPatientRegistration) {
		this.ambPatientRegistration = ambPatientRegistration;
	}

}
