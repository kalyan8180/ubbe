package com.example.test.testingHMS.appointment.dto;

import java.sql.Timestamp;

import javax.persistence.Column;

import com.example.test.testingHMS.appointment.model.SlotTiming;
import com.example.test.testingHMS.user.model.User;

public class DoctorAppointmentDto {
	
	
	

	private String appointmentId;
	
	private String patientName;
	
	private String doctorName;
	
	private String shift;
	
	private String shiftTime;
	
	private String status;
	
	private Timestamp appiointmentDate;
	
	private String fromTime;
	
	private String toTime;
	
	private String createdBy;
	
	
	private long mobileNo;
	
	private String email;
	
	private User appointmentUser;
	
	private SlotTiming slotTiming;
	
	

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SlotTiming getSlotTiming() {
		return slotTiming;
	}

	public void setSlotTiming(SlotTiming slotTiming) {
		this.slotTiming = slotTiming;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getShiftTime() {
		return shiftTime;
	}

	public void setShiftTime(String shiftTime) {
		this.shiftTime = shiftTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public Timestamp getAppiointmentDate() {
		return appiointmentDate;
	}

	public void setAppiointmentDate(Timestamp appiointmentDate) {
		this.appiointmentDate = appiointmentDate;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public User getAppointmentUser() {
		return appointmentUser;
	}

	public void setAppointmentUser(User appointmentUser) {
		this.appointmentUser = appointmentUser;
	}
	
	


}