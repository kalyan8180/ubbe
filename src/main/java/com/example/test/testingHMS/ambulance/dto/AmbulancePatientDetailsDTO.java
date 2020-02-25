package com.example.test.testingHMS.ambulance.dto;

import java.sql.Timestamp;
import java.util.List;

import com.example.test.testingHMS.ambulance.model.AmbulanceServices;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

public class AmbulancePatientDetailsDTO {
	
	
	
private String patAmbulanceId;

private String patName;

private String fromLocation;

private String toLocation;

private long mobileNo;

private float billAmount;

private Timestamp fromTime;

private String ambulanceNo;

private String driverName;

private Timestamp toTime;

private String regNo;


private String paidTo;

private String amountStatus;

private User ambulancePatientUser;


private AmbulanceServices ambulanceServices;

private List<PatientRegistration> ambPatientRegistration;


public Timestamp getToTime() {
	return toTime;
}


public void setToTime(Timestamp toTime) {
	this.toTime = toTime;
}


public String getAmountStatus() {
	return amountStatus;
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
