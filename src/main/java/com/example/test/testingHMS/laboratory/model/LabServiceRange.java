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
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_lab_services_measure_f")
public class LabServiceRange {
	@Id
	@Column(name="measure_id")
	private String measureId;
	
	@Column(name="measure_name")
	private String measureName;
	
	@Column(name="parameter")
	private String parameter;

	@Column(name="method")
	private String method;
	
	@Column(name="age_type")
	private String ageType;
	
	
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="age_limit_min")
	private long ageLimitMin;
	
	@Column(name="age_limit_max")
	private long ageLimitMax;
	
	@Column(name="min_range")
	private String minRange;
	
	@Column(name="max_range")
	private String maxRange;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_date")
	private Timestamp createdDate;
	
	
	@OneToMany(mappedBy="patientLabServiceRange",cascade=CascadeType.ALL)
	private List<PatientServiceDetails> patientServiceDetails;
	
	
	@Column(name="service_id")
	private String labServicesRange;  
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userRange;

	
	
	public List<PatientServiceDetails> getPatientServiceDetails() {
		return patientServiceDetails;
	}
	public void setPatientServiceDetails(List<PatientServiceDetails> patientServiceDetails) {
		this.patientServiceDetails = patientServiceDetails;
	}
	
	public String getLabServicesRange() {
		return labServicesRange;
	}
	public void setLabServicesRange(String labServicesRange) {
		this.labServicesRange = labServicesRange;
	}
	public User getUserRange() {
		return userRange;
	}
	public void setUserRange(User userRange) {
		this.userRange = userRange;
	}
	public String getMeasureId() {
		return measureId;
	}
	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}
	public String getMeasureName() {
		return measureName;
	}
	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public long getAgeLimitMin() {
		return ageLimitMin;
	}
	public void setAgeLimitMin(long ageLimitMin) {
		this.ageLimitMin = ageLimitMin;
	}
	public long getAgeLimitMax() {
		return ageLimitMax;
	}
	public void setAgeLimitMax(long ageLimitMax) {
		this.ageLimitMax = ageLimitMax;
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
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	public String getAgeType() {
		return ageType;
	}
	public void setAgeType(String ageType) {
		this.ageType = ageType;
	}
	@Override
	public String toString() {
		return "LabServiceRange [measureId=" + measureId + ", measureName=" + measureName + ", parameter=" + parameter
				+ ", method=" + method + ", gender=" + gender + ", ageLimitMin=" + ageLimitMin + ", ageLimitMax="
				+ ageLimitMax + ", minRange=" + minRange + ", maxRange=" + maxRange + ", createdBy=" + createdBy
				+ ", createdDate=" + createdDate + ", patientServiceDetails=" + patientServiceDetails
				+ ", labServicesRange=" + labServicesRange + ", userRange=" + userRange + "]";
	}
	
	
	
	

}