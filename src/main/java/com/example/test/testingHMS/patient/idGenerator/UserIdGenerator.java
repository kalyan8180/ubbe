package com.example.test.testingHMS.patient.idGenerator;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator 
{
private String nextUserId;
	
	
	
	
	public UserIdGenerator() {
		super();
	}




	public UserIdGenerator(String nextUserId) {
		super();
		this.nextUserId = nextUserId;
	}




	public String getNextUserId() {
		return nextUserId;
	}




	public void setNextUserId(String nextUserId) {
		this.nextUserId = nextUserId;
	}
	
	
	

}
