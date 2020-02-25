package com.example.test.testingHMS.laboratory.helper;

import org.springframework.stereotype.Component;

@Component
public class RefMeasureDetails 
{
	private String measure;
	
	private String value;
	
	private String dimension;
	
	private String range;
	
	private String method;
	
	private String gender;
	
	
	
	



	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}
	
	

}
