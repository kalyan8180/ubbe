package com.example.test.testingHMS.patient.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="v_cash_card_reg_d")
public class CashPlusCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	
	private String billNo;
	
	private float cashAmount;
	
	private float cardAmount;
	
	private float chequeAmount;
	
	private String description;
	
	private String insertedBy;
	
	
	private Timestamp insertedDate;
	
	@ManyToOne
	@JoinColumn(name="patient_reg")
	private PatientRegistration patientRegistrationCashCard; 

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	
	
	
	public float getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(float cashAmount) {
		this.cashAmount = cashAmount;
	}

	public float getCardAmount() {
		return cardAmount;
	}

	public void setCardAmount(float cardAmount) {
		this.cardAmount = cardAmount;
	}

	public float getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(float chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public void setChequeAmount(long chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public PatientRegistration getPatientRegistrationCashCard() {
		return patientRegistrationCashCard;
	}

	public void setPatientRegistrationCashCard(PatientRegistration patientRegistrationCashCard) {
		this.patientRegistrationCashCard = patientRegistrationCashCard;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public String getInsertedBy() {
		return insertedBy;
	}

	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
	}
	
	
	
	
	
	
	
	
}
