package com.example.test.testingHMS.patient.Helper;

import org.springframework.stereotype.Component;

@Component
public class MultiplePayment {
	
	private String payType; 
	
	private float amount;
	
	
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	

}
