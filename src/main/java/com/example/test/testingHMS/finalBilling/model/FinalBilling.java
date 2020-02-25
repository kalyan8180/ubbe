package com.example.test.testingHMS.finalBilling.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name="v_finalbilling_f")
public class FinalBilling {
	@Id
	@Column(name="bill_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long billId;
	
	@Column(name="bill_no")
	private String billNo;
	
	
	@Column(name="mobile")
	private long mobile;
	
	@Column(name="umr_No")
	private String umrNo;
	
	@Column(name="name")
	private String name;
	
	
	@Column(name="inserted_date")
	private Timestamp insertedDate;
	
	@Column(name="updated_date")
	private Timestamp updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="reg_no")
	private String regNo;
	
	@Column(name="return_amount")
	private float returnAmount;
	
	
	@Column(name="total_amt")
	private float totalAmount;
	
	@Column(name="cash_amt")
	private float cashAmount;
	
	
	@Column(name="card_amt")
	private float cardAmount;

	
	@Column(name="cheque_amt")
	private float chequeAmount;
	
	@Column(name="due_amt")
	private float dueAmount;
	
	@Column(name="disc_amt")
	private float discAmount;
	
	@Column(name="final_amt")
	private float finalAmountPaid;
	
	@Column(name="bill_type")
	private String billType;
	
	@Column(name="payment_type")
	private String paymentType;
	
	@Column(name="due_status")
	private String dueStatus;
	

	@ManyToOne
	@JoinColumn(name="user_id")
	private User finalBillUser;


	
	
	public float getCardAmount() {
		return cardAmount;
	}


	public void setCardAmount(float cardAmount) {
		this.cardAmount = cardAmount;
	}


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public long getBillId() {
		return billId;
	}


	public void setBillId(long billId) {
		this.billId = billId;
	}


	public String getBillNo() {
		return billNo;
	}


	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}


	public String getUmrNo() {
		return umrNo;
	}


	public void setUmrNo(String umrNo) {
		this.umrNo = umrNo;
	}


	public String getRegNo() {
		return regNo;
	}


	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}


	public float getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}


	public float getCashAmount() {
		return cashAmount;
	}


	public void setCashAmount(float cashAmount) {
		this.cashAmount = cashAmount;
	}


	public float getChequeAmount() {
		return chequeAmount;
	}


	public void setChequeAmount(float chequeAmount) {
		this.chequeAmount = chequeAmount;
	}


	public float getDueAmount() {
		return dueAmount;
	}


	public void setDueAmount(float dueAmount) {
		this.dueAmount = dueAmount;
	}


	public float getDiscAmount() {
		return discAmount;
	}


	public void setDiscAmount(float discAmount) {
		this.discAmount = discAmount;
	}


	public float getFinalAmountPaid() {
		return finalAmountPaid;
	}


	public void setFinalAmountPaid(float finalAmountPaid) {
		this.finalAmountPaid = finalAmountPaid;
	}


	public String getBillType() {
		return billType;
	}


	public void setBillType(String billType) {
		this.billType = billType;
	}


	public User getFinalBillUser() {
		return finalBillUser;
	}


	public void setFinalBillUser(User finalBillUser) {
		this.finalBillUser = finalBillUser;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public float getReturnAmount() {
		return returnAmount;
	}


	public void setReturnAmount(float returnAmount) {
		this.returnAmount = returnAmount;
	}


	public Timestamp getInsertedDate() {
		return insertedDate;
	}


	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}


	public Timestamp getUpdatedDate() {
		return updatedDate;
	}


	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}


	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}




	public long getMobile() {
		return mobile;
	}


	public void setMobile(long mobile) {
		this.mobile = mobile;
	}


	public String getDueStatus() {
		return dueStatus;
	}


	public void setDueStatus(String dueStatus) {
		this.dueStatus = dueStatus;
	}
	
	
	
	
	


}
