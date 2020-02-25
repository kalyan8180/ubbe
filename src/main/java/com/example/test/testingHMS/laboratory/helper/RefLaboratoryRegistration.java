package com.example.test.testingHMS.laboratory.helper;

import org.springframework.stereotype.Component;

@Component
public class RefLaboratoryRegistration {
	
	private String serviceName;
	
	private float price;
	
	private float discount;
	
	private float amount;
	
	private long quantity;
	

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	
	



	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	
	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	

	

}
