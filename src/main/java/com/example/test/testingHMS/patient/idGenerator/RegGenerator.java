package com.example.test.testingHMS.patient.idGenerator;

import org.springframework.stereotype.Component;

@Component
public class RegGenerator 
{
	
	private String nextRegId;
	
	
	
	
	public RegGenerator() {
		super();
	}
	
	




	public RegGenerator(String nextRegId) {
		super();
		this.nextRegId = nextRegId;
	}






	public String getNextRegId() {
		return nextRegId;
	}




	public void setNextRegId(String nextRegId) {
		this.nextRegId = nextRegId;
	}

	

}
