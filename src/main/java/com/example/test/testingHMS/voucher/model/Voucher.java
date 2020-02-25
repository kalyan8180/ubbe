package com.example.test.testingHMS.voucher.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.user.model.User;

@Entity(name = "v_voucher_details")
public class Voucher {

	@Id
	@Column(name = "payment_no")
	private String paymentNo;
	
	@Column(name = "payment_date")
	private Timestamp paymentDate;
	
	@Column(name = "bank")
	private String bank;
	
	@Column(name = "check_no")
	private String checkNo;
	
	@Column(name = "check_date")
	private Timestamp checkDate;

	@Column(name = "payment_type")
	private String paymentType;
	
	/*@Column(name = "payment_for")
	private String paymentFor;
	*/
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "voucher_amount")
	private  Long  voucherAmount;
	
	
	
	@Column(name = "prepared_by")
	private String preparedBy;
	
	@Column(name = "printed_by")
	private String printedBy;

	@Column(name = "paid_to")
	private String paidTo;
	
	@Column(name="voucher_type")
	private String voucherType;
	
	@Column(name="raised_by")
	private String raisedBy;
	
	@Column (name="other_name")
	private String otherName;

	transient String voucherDate;
	
	@ManyToOne
	@JoinColumn(name="user_Id")
	private User userVoucher;
	
	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	/*public String getPaymentFor() {
		return paymentFor;
	}

	public void setPaymentFor(String paymentFor) {
		this.paymentFor = paymentFor;
	}*/



	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(Long voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	public Timestamp getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Timestamp paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaidTo() {
		return paidTo;
	}

	public void setPaidTo(String paidTo) {
		this.paidTo = paidTo;
	}

	public String getPreparedBy() {
		return preparedBy;
	}

	public void setPreparedBy(String preparedBy) {
		this.preparedBy = preparedBy;
	}


	public String getPrintedBy() {
		return printedBy;
	}

	public void setPrintedBy(String printedBy) {
		this.printedBy = printedBy;
	}

	
	
	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public Timestamp getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Timestamp checkDate) {
		this.checkDate = checkDate;
	}

	

	

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public User getUserVoucher() {
		return userVoucher;
	}

	public void setUserVoucher(User userVoucher) {
		this.userVoucher = userVoucher;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(String raisedBy) {
		this.raisedBy = raisedBy;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}

	
	
	
}