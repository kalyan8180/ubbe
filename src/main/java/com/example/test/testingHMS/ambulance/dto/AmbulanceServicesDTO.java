package com.example.test.testingHMS.ambulance.dto;

import com.example.test.testingHMS.user.model.User;

public class AmbulanceServicesDTO {

	private long ambulanceId;
	private float cost;
	private String service;

	private String ambulanceNO;
private int status;

	private User ambulanceUser;
	
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAmbulanceNO() {
		return ambulanceNO;
	}
	public void setAmbulanceNO(String ambulanceNO) {
		this.ambulanceNO = ambulanceNO;
	}
		public long getAmbulanceId() {
		return ambulanceId;
	}
	public void setAmbulanceId(long ambulanceId) {
		this.ambulanceId = ambulanceId;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public User getAmbulanceUser() {
		return ambulanceUser;
	}
	public void setAmbulanceUser(User ambulanceUser) {
		this.ambulanceUser = ambulanceUser;
	}
	

	
	
}
