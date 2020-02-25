package com.example.test.testingHMS.user.dto;

import com.example.test.testingHMS.user.model.User;

public class DoctorFeeDetailsDto {
	
    private long feeId;

	private long fee;

	private String doctorName;
	
	private User userFee;

	public long getFeeId() {
		return feeId;
	}


	public void setFeeId(long feeId) {
		this.feeId = feeId;
	}


	public long getFee() {
		return fee;
	}


	public void setFee(long fee) {
		this.fee = fee;
	}


	public String getDoctorName() {
		return doctorName;
	}


	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}


	public User getUserFee() {
		return userFee;
	}


	public void setUserFee(User userFee) {
		this.userFee = userFee;
	}

	

	
}