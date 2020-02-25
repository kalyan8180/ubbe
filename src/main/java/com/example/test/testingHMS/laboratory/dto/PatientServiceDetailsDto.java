package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;

import com.example.test.testingHMS.laboratory.helper.RefMeasureDetails;
import com.example.test.testingHMS.laboratory.model.LabServiceRange;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

public class PatientServiceDetailsDto {
	
	private String patServiceId;
	
	private String actualValue;
	
	private String createdBy;
	
	private String comment;
	
	private Timestamp createdDate;
	
	private transient String serviceName;
	
	private transient String regId;
	
	private transient List<RefMeasureDetails> refMeasureDetails;
	
	private User userPatientService;
	
	private PatientRegistration patientService;
	
	private LabServices patientLabService;
	
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

	
	
	
	

}