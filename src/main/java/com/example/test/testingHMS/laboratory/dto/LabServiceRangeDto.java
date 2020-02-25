package com.example.test.testingHMS.laboratory.dto;


import java.sql.Timestamp;
import java.util.List;

import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.PatientServiceDetails;
import com.example.test.testingHMS.user.model.User;

public class LabServiceRangeDto {

	private String measureId;
	
	private String measureName;
	
	private String parameter;
	
	private long ageLimitMin;
	
	private long ageLimitMax;
	
	private String minRange;
	
	private String maxRange;
	
	private String ageType;
	
	private String createdBy;
	
	private String gender;
	
	private String method;
	
	private Timestamp createdDate;
	
	
	private List<PatientServiceDetails> patientServiceDetails;
	
	private String labServicesRange;  
	
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getAgeType() {
		return ageType;
	}
	public void setAgeType(String ageType) {
		this.ageType = ageType;
	}
	
	
	

}