package com.example.test.testingHMS.nurse.dto;

import java.sql.Timestamp;

import com.example.test.testingHMS.patient.model.PatientDetails;


public class PrescriptionDetailsDto {

	private String prescriptionId;



	private byte[] fileName;

	private String fileDownloadUri;
	
	private String doctorName;

	private Timestamp createdAt;

	private Timestamp modifiedAt;

	private String uploadedById;
	
	private String umr;
	
	private  PatientDetails  patientDetails;

	

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}


	

	public byte[] getFileName() {
		return fileName;
	}

	public void setFileName(byte[] fileName) {
		this.fileName = fileName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDactorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Timestamp modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getUploadedById() {
		return uploadedById;
	}

	public void setUploadedById(String uploadedById) {
		this.uploadedById = uploadedById;
	}

	public PatientDetails getPatientDetails() {
		return patientDetails;
	}

	public void setPatientDetails(PatientDetails patientDetails) {
		this.patientDetails = patientDetails;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}
	
	
	
	

}
