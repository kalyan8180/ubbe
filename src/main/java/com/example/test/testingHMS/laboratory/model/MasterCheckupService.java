package com.example.test.testingHMS.laboratory.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name="v_master_checkup_service_d")
public class MasterCheckupService {
	 @Id
	 @Column(name="master_checkup_id") 
	 private String masterCheckupId;
	 
	 @Column(name="cost")
	 private float cost;
	
	
	@Column(name="checkup_id")
	private String checkupId;
	
		
	@Column(name="service_name")
	private String serviceName;
	
	@Column(name="master_service_name")
	private String masterServiceName;
	
	@Column(name="created_date")
	private Timestamp createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="modified_date")
	private Timestamp modifiedDate;
	
	@Column(name="master_service_id")
	private String masterServiceId;
	
	
	
	private transient List<Map<String, String>> addCheckUp;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User masterCheckUpUser;
	
	

	@OneToMany(mappedBy="masterCheckupService",cascade=CascadeType.ALL)
	private List<MasterCheckUpRegistration> masterCheckUpRegistration;
	

	public String getMasterCheckupId() {
		return masterCheckupId;
	}

	public void setMasterCheckupId(String masterCheckupId) {
		this.masterCheckupId = masterCheckupId;
	}

	public String getCheckupId() {
		return checkupId;
	}

	public void setCheckupId(String checkupId) {
		this.checkupId = checkupId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMasterServiceName() {
		return masterServiceName;
	}

	public void setMasterServiceName(String masterServiceName) {
		this.masterServiceName = masterServiceName;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getMasterServiceId() {
		return masterServiceId;
	}

	public void setMasterServiceId(String masterServiceId) {
		this.masterServiceId = masterServiceId;
	}

	public User getMasterCheckUpUser() {
		return masterCheckUpUser;
	}

	public void setMasterCheckUpUser(User masterCheckUpUser) {
		this.masterCheckUpUser = masterCheckUpUser;
	}

	public List<Map<String, String>> getAddCheckUp() {
		return addCheckUp;
	}

	public void setAddCheckUp(List<Map<String, String>> addCheckUp) {
		this.addCheckUp = addCheckUp;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public List<MasterCheckUpRegistration> getMasterCheckUpRegistration() {
		return masterCheckUpRegistration;
	}

	public void setMasterCheckUpRegistration(List<MasterCheckUpRegistration> masterCheckUpRegistration) {
		this.masterCheckUpRegistration = masterCheckUpRegistration;
	}

	
}