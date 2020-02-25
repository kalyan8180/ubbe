package com.example.test.testingHMS.ambulance.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name="v_ambulance_service_d")
public class AmbulanceServices {

	@Id
	@Column(name="ambulance_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long ambulanceId;
	@Column(name="cost")
	private float cost;
	@Column(name="service")
	private String service;
	@Column(name="ambulance_no")
	private String ambulanceNO;
	
	@Column(name="status")
	private int status;
	
	
	@ManyToOne
	@JoinColumn(name="user_id")
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
		public User getAmbulanceUser() {
		return ambulanceUser;
	}
	public void setAmbulanceUser(User ambulanceUser) {
		this.ambulanceUser = ambulanceUser;
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
	
	
	
}
