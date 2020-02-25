package com.example.test.testingHMS.pharmacist.helper;

import org.springframework.stereotype.Component;

@Component
public class RefVendorIds 
{
	String vendorId;
	
	String regId;

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	

}
