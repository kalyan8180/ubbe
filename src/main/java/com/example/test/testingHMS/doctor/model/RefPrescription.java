package com.example.test.testingHMS.doctor.model;

import org.springframework.stereotype.Component;

@Component
public class RefPrescription {
	
	private String patientName;
	private String regId;
	private long mobileNo;
	private String doj;
	private String gender;
	private String consultaionDetails;
	private String presentillness;
	private String physicalExamination;
	private String investigationAdviced;
	private String medicationNameDosage;
	private String patientInstruction;
	private String recommendation;
	private String docId;
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public long getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getDoj() {
		return doj;
	}
	public void setDoj(String doj) {
		this.doj = doj;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getConsultaionDetails() {
		return consultaionDetails;
	}
	public void setConsultaionDetails(String consultaionDetails) {
		this.consultaionDetails = consultaionDetails;
	}
	public String getPresentillness() {
		return presentillness;
	}
	public void setPresentillness(String presentillness) {
		this.presentillness = presentillness;
	}
	public String getPhysicalExamination() {
		return physicalExamination;
	}
	public void setPhysicalExamination(String physicalExamination) {
		this.physicalExamination = physicalExamination;
	}
	public String getInvestigationAdviced() {
		return investigationAdviced;
	}
	public void setInvestigationAdviced(String investigationAdviced) {
		this.investigationAdviced = investigationAdviced;
	}
	public String getMedicationNameDosage() {
		return medicationNameDosage;
	}
	public void setMedicationNameDosage(String medicationNameDosage) {
		this.medicationNameDosage = medicationNameDosage;
	}
	public String getPatientInstruction() {
		return patientInstruction;
	}
	public void setPatientInstruction(String patientInstruction) {
		this.patientInstruction = patientInstruction;
	}
	public String getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	
	

}
