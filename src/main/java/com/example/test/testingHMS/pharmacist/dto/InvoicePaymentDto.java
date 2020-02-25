package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;

public class InvoicePaymentDto {

	private String paymentId;

	private String invoiceId;

	private String paidBy;

	private Timestamp date;

	private String paidInFull;

	private long amount;

	private Timestamp insertedDate;

	private String chequeNo;

	private String cardTxnNo;

	private String pId;

	private String userId;

	private String bankTxnNo;

	private String insertedby;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(String paidBy) {
		this.paidBy = paidBy;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getPaidInFull() {
		return paidInFull;
	}

	public void setPaidInFull(String paidInFull) {
		this.paidInFull = paidInFull;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getCardTxnNo() {
		return cardTxnNo;
	}

	public void setCardTxnNo(String cardTxnNo) {
		this.cardTxnNo = cardTxnNo;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBankTxnNo() {
		return bankTxnNo;
	}

	public void setBankTxnNo(String bankTxnNo) {
		this.bankTxnNo = bankTxnNo;
	}

	public String getInsertedby() {
		return insertedby;
	}

	public void setInsertedby(String insertedby) {
		this.insertedby = insertedby;
	}

}
