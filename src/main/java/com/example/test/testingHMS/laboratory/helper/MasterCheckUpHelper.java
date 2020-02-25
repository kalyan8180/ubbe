package com.example.test.testingHMS.laboratory.helper;

import org.springframework.stereotype.Component;

@Component
public class MasterCheckUpHelper {
	
	private String serviceName;
	private float cost;
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}

	
}
