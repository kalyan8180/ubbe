package com.example.test.testingHMS.patient.idGenerator;

import org.springframework.stereotype.Component;

@Component
public class UmrGenerator 
{
	
	private String umr;
	
	
	
	
	public UmrGenerator() {
		super();
	}

	public UmrGenerator(String umr) {
		super();
		this.umr = umr;
	}

	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
	}
	
	

}
