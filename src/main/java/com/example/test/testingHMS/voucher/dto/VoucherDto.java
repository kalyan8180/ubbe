package com.example.test.testingHMS.voucher.dto;

import java.sql.Timestamp;

import com.example.test.testingHMS.user.model.User;
public class VoucherDto {
	
	private String paymentNo;
	
	/*private String paymentFor;*/
	
	
	
	private String remarks;
	
	private  Long  voucherAmount;
	
	private Timestamp paymentDate;
	
	private String paidTo;

	private String preparedBy;
	
	
	private String printedBy;


	private String bank;
	
	private String checkNo;
	
	private Timestamp checkDate;

	private String paymentType;
	
	private String voucherType;
	
	private String raisedBy;
	
	private String otherName;
	
	private User userVoucher;
	
	transient String voucherDate;
	
	
	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	
	
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