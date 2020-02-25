package com.example.test.testingHMS.laboratory.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.laboratory.helper.RefMeasureDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_patient_services_details_f")
public class PatientServiceDetails {
	
	@Id
	@Column(name="pat_service_id")
	private String patServiceId;
	
	@Column(name="actual_value")
	private String actualValue;

	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="comment")
	private String comment;
	
	@Column(name="created_date")
	private Timestamp createdDate;
	
	private transient String serviceName;
	
	private transient String regId;
	
	private transient String minRange;
	
	private transient String dimension;
	
	private transient String maxRange;
	
	private transient List<RefMeasureDetails> refMeasureDetails;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userPatientService;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="p_reg_id")
	private PatientRegistration patientService;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="service_id")
	private LabServices patientLabService;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="measure_id")
	private LabServiceRange patientLabServiceRange;
	
	public User getUserPatientService() {
		return userPatientService;
	}
	public void setUserPatientService(User userPatientService) {
		this.userPatientService = userPatientService;
	}
	
	public PatientRegistration getPatientService() {
		return patientService;
	}
	public void setPatientService(PatientRegistration patientService) {
		this.patientService = patientService;
	}
	public LabServices getPatientLabService() {
		return patientLabService;
	}
	public void setPatientLabService(LabServices patientLabService) {
		this.patientLabService = patientLabService;
	}
	
	public LabServiceRange getPatientLabServiceRange() {
		return patientLabServiceRange;
	}
	public void setPatientLabServiceRange(LabServiceRange patientLabServiceRange) {
		this.patientLabServiceRange = patientLabServiceRange;
	}
	public String getPatServiceId() {
		return patServiceId;
	}
	public void setPatServiceId(String patServiceId) {
		this.patServiceId = patServiceId;
	}
	
	
	public String getActualValue() {
		return actualValue;
	}
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public List<RefMeasureDetails> getRefMeasureDetails() {
		return refMeasureDetails;
	}
	public void setRefMeasureDetails(List<RefMeasureDetails> refMeasureDetails) {
		this.refMeasureDetails = refMeasureDetails;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getMinRange() {
		return minRange;
	}
	public void setMinRange(String minRange) {
		this.minRange = minRange;
	}
	public String getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}
	public String getDimension() {
		return dimension;
	}
	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	
	
	

}