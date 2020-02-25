package com.example.test.testingHMS.user.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="v_doctor_fee_d")
public class DoctorFeeDetails implements Serializable {
	
	@Id
	@Column(name="fee_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
     private long feeId;
	
	@Column(name="fee")
	private long fee;
	
	@Column(name="doctor_name")
	private String doctorName;
	
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "user_id")
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