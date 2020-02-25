package com.example.test.testingHMS.pharmacist.dto;

import com.example.test.testingHMS.pharmacist.model.MedicineDetails;

public class MedicineQuantityDto {

	private long inventoryId;
	
	private float totalQuantity;
	
	private String batchId;
	
	private long sold;
	
	
	private float balance;
	
	private String medName;
	
	private MedicineDetails medicineId;

	public long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(long inventoryId) {
		this.inventoryId = inventoryId;
	}

	

	public long getSold() {
		return sold;
	}

	public void setSold(long sold) {
		this.sold = sold;
	}

	
	public float getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(float totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public MedicineDetails getMedicineId() {
		return medicineId;
	}

	public void setMedicineId(MedicineDetails medicineId) {
		this.medicineId = medicineId;
	}

	public String getMedName() {
		return medName;
	}

	public void setMedName(String medName) {
		this.medName = medName;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	
}

