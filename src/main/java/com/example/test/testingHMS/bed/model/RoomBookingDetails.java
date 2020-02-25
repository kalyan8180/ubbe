package com.example.test.testingHMS.bed.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "v_room_booking_details_f")
public class RoomBookingDetails {
	@Id
	@Column(name = "booking_id")
	private String bookingId;
	
	@Column(name = "from_date")
	private Timestamp fromDate;
	
	@Column(name = "to_date")
	private Timestamp toDate;
	
	@Column(name = "tentative_discharge_date")
	private Timestamp tentativeDischargeDate;
	
	@Column(name = "bed_no")
	private String bedNo;
	
	@Column(name = "status")
	private int status;
	
	@Column(name="revoke_status")
	private String revokeStatus;
	
	private int costSoFar;
	
	public transient long advanceAmount;
	
	public transient long estimateAmount;

	@ManyToOne
	@JoinColumn(name = "bed_id")
	private RoomDetails roomDetails;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "p_reg_id")
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
