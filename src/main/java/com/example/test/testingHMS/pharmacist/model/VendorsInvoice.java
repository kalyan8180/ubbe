package com.example.test.testingHMS.pharmacist.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_vendor_invoice")
public class VendorsInvoice {

	@Id
	@Column(name="invoice_id")
	private String invoiceId ;
		
	@Column(name="quantity")
	private long quantity;
		
	@Column(name="date")
	private  Timestamp date;
	
	@Column(name="paid_in_full")
	private String paidInFull;
	
	@Column(name="balance_amount")
	private long balanceAmount;
	
	@Column(name="paidAmount")
	private long paid_amount;
	
	@Column(name="due_amount")
	private long dueAmount;
	
	@Column(name="cheque_no")
	private String chequeNo;
	
	@Column(name="card_txn_no")
	private String cardTxnNo;
	
	@Column(name="payment_type")
	private String paymentType;
	
	
	@Column(name="bank_txn_no")
	private String bankTxnNo;
	
	@Column(name="payment_date")
	private Timestamp paymentDate;
	
	@Column(name="entered_by_id")
	private String enteredById ;
	
	
	private transient String location;
	
	@Column(name="procurement_id")
	private String vendorInvoiceMedicineProcurement;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_Id")
	private User vendorInvoiceUser;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location  vendorInvoiceLocation;
	
	
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
	public Location getVendorInvoiceLocation() {
		return vendorInvoiceLocation;
	}
	public void setVendorInvoiceLocation(Location vendorInvoiceLocation) {
		this.vendorInvoiceLocation = vendorInvoiceLocation;
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