package com.example.test.testingHMS.doctor.dto;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RefDoctorDetails 
{
	private String regNo;
	
	private String patientName;
	
	private String doctorName;
	
	private String prescription;
	
	private String doj;
	
	private String notes;
	
	private List<Map<String,String>> report;
	
	

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}


	


	

	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}



	public List<Map<String, String>> getReport() {
		return report;
	}

	public void setReport(List<Map<String, String>> report) {
		this.report = report;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getDoj() {
		return doj;
	}

	public void setDoj(String doj) {
		this.doj = doj;
	}


	
	
	
	

}
