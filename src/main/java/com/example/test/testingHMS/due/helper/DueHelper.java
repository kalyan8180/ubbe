package com.example.test.testingHMS.due.helper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.test.testingHMS.patient.Helper.MultiplePayment;

@Component
public class DueHelper {
	
	private String mode;
	
	private String dueFor;
	
	private  List<Map<String,String>> multimode; 
	
	private float amount;
	
   private String referenceNumber;
   
   private float netAmount;
   
   private float discount;
   
   private String date;
   
   private  List<MultiplePayment> multiplePayment;
   
   

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}



	public String getDueFor() {
		return dueFor;
	}

	public void setDueFor(String dueFor) {
		this.dueFor = dueFor;
	}

	public List<Map<String, String>> getMultimode() {
		return multimode;
	}

	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public float getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(float netAmount) {
		this.netAmount = netAmount;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public List<MultiplePayment> getMultiplePayment() {
		return multiplePayment;
	}

	public void setMultiplePayment(List<MultiplePayment> multiplePayment) {
		this.multiplePayment = multiplePayment;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

		
	
	
	

}