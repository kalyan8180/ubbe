package com.example.test.testingHMS.laboratory.model;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_lab_services_d")
public class LabServices {
	
	@Id
	@Column(name="service_id")
	private String serviceId;
	
	@Column(name="service_name")
	private String serviceName;
	
	@Column(name="room_type")
	private String roomtype;
	
	@Column(name="master_service_id")
	private String masterServiceId;
	
	@Column(name="patient_type")
	private String patientType;
	
	@Column(name="department")
	private String department;
	
	@Column(name="service_type")
	private String serviceType;
	
	@Column(name="inserted_date")
	private Timestamp insertedDate;
	
	@Column(name="cost")
	private long cost;
	
	@Column(name="from_date")
	private Timestamp fromDate;
	
	@Column(name="still_date")
	private Timestamp tillDate;

	@Column(name="in_house")
	private String inHouse;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="specimenType")
	private String specimenType;
	
	private transient List<Map<String, String>> addService;
	
	@JsonIgnore
	@OneToMany(mappedBy="patientLabService",cascade=CascadeType.ALL)
	private List<PatientServiceDetails> patientServiceDetails;
	
	@JsonIgnore
	@OneToMany(mappedBy="serviceId",cascade=CascadeType.ALL)
	private List<ChargeBill> chargeBill;
	
	
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userLabService;
	
	
		
	public List<PatientServiceDetails> getPatientServiceDetails() {
		return patientServiceDetails;
	}
	public void setPatientServiceDetails(List<PatientServiceDetails> patientServiceDetails) {
		this.patientServiceDetails = patientServiceDetails;
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