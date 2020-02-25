package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.user.model.User;


public class VendorsInvoiceDto {
	private String invoiceId ;
		
	private long quantity;
		
	private  Timestamp date;
	
	private String paidInFull;
	
	private long balanceAmount;
	
	private long paid_amount;
	
	private long dueAmount;
	
	private String paymentType;
	
	private String chequeNo;
	
	private String cardTxnNo;
	
	private String bankTxnNo;
	
	private Timestamp paymentDate;
	
	private String enteredById ;
	
	private String vendorInvoiceMedicineProcurement;
	
	private transient String location;
	
	private User vendorInvoiceUser;
	
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
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
	public long getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(long balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public long getPaid_amount() {
		return paid_amount;
	}
	public void setPaid_amount(long paid_amount) {
		this.paid_amount = paid_amount;
	}
	public long getDueAmount() {
		return dueAmount;
	}
	public void setDueAmount(long dueAmount) {
		this.dueAmount = dueAmount;
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
	public String getBankTxnNo() {
		return bankTxnNo;
	}
	public void setBankTxnNo(String bankTxnNo) {
		this.bankTxnNo = bankTxnNo;
	}
	public Timestamp getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Timestamp paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getEnteredById() {
		return enteredById;
	}
	public void setEnteredById(String enteredById) {
		this.enteredById = enteredById;
	}
	
	public User getVendorInvoiceUser() {
		return vendorInvoiceUser;
	}
	public void setVendorInvoiceUser(User vendorInvoiceUser) {
		this.vendorInvoiceUser = vendorInvoiceUser;
	}
	public String getVendorInvoiceMedicineProcurement() {
		return vendorInvoiceMedicineProcurement;
	}
	public void setVendorInvoiceMedicineProcurement(String vendorInvoiceMedicineProcurement) {
		this.vendorInvoiceMedicineProcurement = vendorInvoiceMedicineProcurement;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	
	
}
