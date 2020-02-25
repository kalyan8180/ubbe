package com.example.test.testingHMS.appointment.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_doctor_appointment_f")
public class DoctorAppointment {
	
	@Id
	@Column(name="appointment_id")
	private String appointmentId;
	
	@Column(name="patient_name")
	private String patientName;
	
	@Column(name="doctor_name")
	private String doctorName;
	
	@Column(name="shift")
	private String shift;
	
	@Column(name="shift_time")
	private String shiftTime;
	
	@Column(name="status")
	private String status;
	
	@Column(name="mobile_no")
	private long mobileNo;
	
	@Column(name="email")
	private String email;
	
	@Column(name="appointment_date")
	private Timestamp appiointmentDate;
	
	@Column(name="from_time")
	private String fromTime;
	
	@Column(name="to_time")
	private String toTime;
	
	@Column(name="created_by")
	private String createdBy;
	

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id")
	private User appointmentUser;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="slot_id")
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



	public String getAppointmentId() {
		return appointmentId;
	}



	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}



	public String getPatientName() {
		return patientName;
	}



	public SlotTiming getSlotTiming() {
		return slotTiming;
	}



	public void setSlotTiming(SlotTiming slotTiming) {
		this.slotTiming = slotTiming;
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