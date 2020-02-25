package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSalesReturn;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SalesRefundDto 
{
	private String returnId;
	
	private float returnAmount;
	
	private String umr;
	
	private String status;
	
	private String name;
	
	private long mobileNo;
	
	private float amount;
	
	private String paymentType;
	
	private String refundBy;
	
	private Timestamp refundDate;
	
	private String billNo;
	
	private transient List<RefSalesReturn> refSalesReturns;
	
	private User salesRefundUser;
	
    private PatientRegistration salesRefundPatientRegistration;
    
	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}


	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}


	
	public float getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(float returnAmount) {
		this.returnAmount = returnAmount;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getRefundBy() {
		return refundBy;
	}

	public void setRefundBy(String refundBy) {
		this.refundBy = refundBy;
	}

	public Timestamp getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Timestamp refundDate) {
		this.refundDate = refundDate;
	}

	public User getSalesRefundUser() {
		return salesRefundUser;
	}

	public void setSalesRefundUser(User salesRefundUser) {
		this.salesRefundUser = salesRefundUser;
	}

	

	public PatientRegistration getSalesRefundPatientRegistration() {
		return salesRefundPatientRegistration;
	}

	public void setSalesRefundPatientRegistration(PatientRegistration salesRefundPatientRegistration) {
		this.salesRefundPatientRegistration = salesRefundPatientRegistration;
	}

	
	public List<RefSalesReturn> getRefSalesReturns() {
		return refSalesReturns;
	}

	public void setRefSalesReturns(List<RefSalesReturn> refSalesReturns) {
		this.refSalesReturns = refSalesReturns;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    

}
