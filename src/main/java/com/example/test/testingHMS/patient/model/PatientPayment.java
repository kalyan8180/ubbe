package com.example.test.testingHMS.patient.model;



import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_patient_payment_f")
public class PatientPayment 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long paymentId;
	
	
	@Column(name="amount")
	private long amount;
	
	@Column(name="inserted_date")
	private Timestamp insertedDate;
	
	@Column(name="modified_date")
	private Timestamp modifiedDate;
	
	@Column(name="reference_number")
	private String referenceNumber;
	
	@Column(name="type_of_charge")
	private String typeOfCharge;
	
	@Column(name="billNo")
	private String billNo;
	
	@Column(name="mode_of_payment")
	private String modeOfPaymant;
	
	@Column(name="paid")
	private String paid;
	
	@Column(name="raised_by_id")
	private String raisedById;
	
	@Column(name="description")
	private String description;
	
	
	@Column(name="ip_settled_flag")
	private String ipSettledFlag;
	
	private transient List<MultiplePayment> multiplePayment;
	
	@JsonBackReference
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="reg_id")
	private PatientRegistration patientRegistration ;
	
	


	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
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

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getTypeOfCharge() {
		return typeOfCharge;
	}

	public void setTypeOfCharge(String typeOfCharge) {
		this.typeOfCharge = typeOfCharge;
	}

	public String getModeOfPaymant() {
		return modeOfPaymant;
	}

	public void setModeOfPaymant(String modeOfPaymant) {
		this.modeOfPaymant = modeOfPaymant;
	}

	public PatientRegistration getPatientRegistration() {
		return patientRegistration;
	}

	public void setPatientRegistration(PatientRegistration patientRegistration) {
		this.patientRegistration = patientRegistration;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getRaisedById() {
		return raisedById;
	}

	public void setRaisedById(String raisedById) {
		this.raisedById = raisedById;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getIpSettledFlag() {
		return ipSettledFlag;
	}

	public void setIpSettledFlag(String ipSettledFlag) {
		this.ipSettledFlag = ipSettledFlag;
	}

	public List<MultiplePayment> getMultiplePayment() {
		return multiplePayment;
	}

	public void setMultiplePayment(List<MultiplePayment> multiplePayment) {
		this.multiplePayment = multiplePayment;
	}


}
