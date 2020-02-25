package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.laboratory.model.LabServiceRange;
import com.example.test.testingHMS.laboratory.model.PatientServiceDetails;
import com.example.test.testingHMS.user.model.User;

public class LabServicesDto {
	
	private String serviceId;

	private String serviceName;

	private String patientType;
	
	private String roomtype;

	private String department;
	
	private String serviceType;
	
	private String masterServiceId;

	private Timestamp insertedDate;
	
	private long cost;

	private Timestamp fromDate;

	private Timestamp tillDate;

	private String inHouse;

	private String createdBy;
	
	private List<PatientServiceDetails> patientServiceDetails;
	
	private transient List<Map<String, String>> addService;
	
	private List<LabServiceRange> labServiceRange;
	
	private User userLabService;
	
	private String specimenType;
	
	private List<ChargeBill> chargeBill;
	
	public List<PatientServiceDetails> getPatientServiceDetails() {
		return patientServiceDetails;
	}
	public void setPatientServiceDetails(List<PatientServiceDetails> patientServiceDetails) {
		this.patientServiceDetails = patientServiceDetails;
	}
	public List<LabServiceRange> getLabServiceRange() {
		return labServiceRange;
	}
	public void setLabServiceRange(List<LabServiceRange> labServiceRange) {
		this.labServiceRange = labServiceRange;
	}
	
	
	public long getCost() {
		return cost;
	}
	public void setCost(long cost) {
		this.cost = cost;
	}
	
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getTillDate() {
		return tillDate;
	}
	public void setTillDate(Timestamp tillDate) {
		this.tillDate = tillDate;
	}
	public User getUserLabService() {
		return userLabService;
	}
	public void setUserLabService(User userLabService) {
		this.userLabService = userLabService;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getPatientType() {
		return patientType;
	}
	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Timestamp getInsertedDate() {
		return insertedDate;
	}
	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}
	public String getInHouse() {
		return inHouse;
	}
	public void setInHouse(String inHouse) {
		this.inHouse = inHouse;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public List<ChargeBill> getChargeBill() {
		return chargeBill;
	}
	public void setChargeBill(List<ChargeBill> chargeBill) {
		this.chargeBill = chargeBill;
	}
	public String getSpecimenType() {
		return specimenType;
	}
	public void setSpecimenType(String specimenType) {
		this.specimenType = specimenType;
	}
	
	public String getMasterServiceId() {
		return masterServiceId;
	}
	public void setMasterServiceId(String masterServiceId) {
		this.masterServiceId = masterServiceId;
	}
	public String getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}
	public List<Map<String, String>> getAddService() {
		return addService;
	}
	public void setAddService(List<Map<String, String>> addService) {
		this.addService = addService;
	}
	
	
	
	
	
	
	
}