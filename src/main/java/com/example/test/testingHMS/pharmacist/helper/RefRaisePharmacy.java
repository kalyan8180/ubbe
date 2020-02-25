package com.example.test.testingHMS.pharmacist.helper;

import org.springframework.stereotype.Component;

@Component
public class RefRaisePharmacy 
{
	private String regNo;
	
	private String medicineList;

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getMedicineList() {
		return medicineList;
	}

	public void setMedicineList(String medicineList) {
		this.medicineList = medicineList;
	}
	

	

}
