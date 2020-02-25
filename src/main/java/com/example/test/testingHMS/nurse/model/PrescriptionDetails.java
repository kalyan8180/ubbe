package com.example.test.testingHMS.nurse.model;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Entity
@Table(name = "v_patient_prescription_details_f")
public class PrescriptionDetails {
	@Id
	@Column(name = "prescription_id")
	private String prescriptionId;

	@JsonIgnore
	@Column(name = "file_name")
	@Lob
	private byte[] fileName;
	
	@JsonIgnore
	private String fileNamee;

	@JsonIgnore
	private String fileType;
	
	
	@Column(name="download_uri")
	private String fileDownloadUri;

	@JsonIgnore
	@Column(name = "doctor_id")
	private String doctorName;

	@JsonIgnore
	@Column(name = "created_at")
	private Timestamp createdAt;

	@JsonIgnore
	@Column(name = "modified_at")
	private Timestamp modifiedAt;

	@JsonIgnore
	@Column(name = "uploaded_by_Id")
	private String uploadedById;
	
	
	@JsonIgnore
	private String regId;

	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="p_reg_Id")
	private  PatientRegistration  patientRegistration;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="p_Id")
	private  User  userDetails;
	

	
	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	

	
	

	public PatientRegistration getPatientRegistration() {
		return patientRegistration;
	}

	public void setPatientRegistration(PatientRegistration patientRegistration) {
		this.patientRegistration = patientRegistration;
	}

	public User getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(User userDetails) {
		this.userDetails = userDetails;
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

	public void setDoctorName(String doctorName) {
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

	

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

	public String getFileNamee() {
		return fileNamee;
	}

	public void setFileNamee(String fileNamee) {
		this.fileNamee = fileNamee;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		return "PrescriptionDetails [prescriptionId=" + prescriptionId + ", fileName=" + Arrays.toString(fileName)
				+ ", fileNamee=" + fileNamee + ", fileType=" + fileType + ", fileDownloadUri=" + fileDownloadUri
				+ ", doctorName=" + doctorName + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt
				+ ", uploadedById=" + uploadedById + ", regId=" + regId + ", patientRegistration=" + patientRegistration
				+ ", userDetails=" + userDetails + "]";
	}

	
	
	
	
	

}
