package com.example.test.testingHMS.bed.dto;

import java.sql.Timestamp;

import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;

public class RoomBookingDetailsDTO {

	private String bookingId;
	
	private Timestamp fromDate;
	
	private Timestamp toDate;
	
	private String revokeStatus;
	
	private Timestamp tentativeDischargeDate;	
	
	private RoomDetails roomDetails;
	
	private String bedNo;

	private int costSoFar;
	
	public transient long advanceAmount;
	
	public transient long estimateAmount;

	private PatientRegistration patientRegistrationBooking;
	
	
	public String getBedNo() {
		return bedNo;
	}
	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}
	public String getBookingId() {
		return bookingId;
	}
	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public Timestamp getTentativeDischargeDate() {
		return tentativeDischargeDate;
	}
	public void setTentativeDischargeDate(Timestamp tentativeDischargeDate) {
		this.tentativeDischargeDate = tentativeDischargeDate;
	}
	public RoomDetails getRoomDetails() {
		return roomDetails;
	}
	public void setRoomDetails(RoomDetails roomDetails) {
		this.roomDetails = roomDetails;
	}
	public PatientRegistration getPatientRegistrationBooking() {
		return patientRegistrationBooking;
	}
	public void setPatientRegistrationBooking(PatientRegistration patientRegistrationBooking) {
		this.patientRegistrationBooking = patientRegistrationBooking;
	}
	public long getAdvanceAmount() {
		return advanceAmount;
	}
	public void setAdvanceAmount(long advanceAmount) {
		this.advanceAmount = advanceAmount;
	}
	public long getEstimateAmount() {
		return estimateAmount;
	}
	public void setEstimateAmount(long estimateAmount) {
		this.estimateAmount = estimateAmount;
	}
	public int getCostSoFar() {
		return costSoFar;
	}
	public void setCostSoFar(int costSoFar) {
		this.costSoFar = costSoFar;
	}
	public String getRevokeStatus() {
		return revokeStatus;
	}
	public void setRevokeStatus(String revokeStatus) {
		this.revokeStatus = revokeStatus;
	}
	
	
	
	
	
}
