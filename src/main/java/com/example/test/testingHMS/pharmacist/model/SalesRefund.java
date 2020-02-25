package com.example.test.testingHMS.pharmacist.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSalesReturn;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_sales_refund")
public class SalesRefund {
	@Id
	@Column(name="return_id")
	private String returnId;
	
	@Column(name="return_amt")
	private float returnAmount;
	
	@Column(name="umr")
	private String umr;
	
	@Column(name="status")
	private String status;
	
	@Column(name="name")
	private String name;
	
	@Column(name="mobile_no")
	private long mobileNo;
	
	@Column(name="amount")
	private float amount;
	
	@Column(name="payment_type")
	private String paymentType;
	
	@Column(name="refund_by")
	private String refundBy;
	
	@Column(name="refund_date")
	private Timestamp refundDate;
	
	@Column(name="bill_no")
	private String billNo;
	
	private transient List<RefSalesReturn> refSalesReturns;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_Id")
	private User salesRefundUser;
	
	@JsonIgnore
    @ManyToOne
    @JoinColumn(name="p_reg_id")
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