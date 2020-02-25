package com.example.test.testingHMS.pharmacist.helper;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class RefMedicine {
	

	private String name;
	

	private String itemLevel;
	

	private String batchNo;
	

	private String manufacturer;
	

	private String vendorPackage;
	

	private String brand;
	

	private String drugType;
	

	private String strengthUnits;
	

	private Timestamp insertedDate;
	

	private Timestamp modifiedDate;
	

	private String insertedBy;
	

	private String modifiedBy;
	

	private long saleUnits;
	

	private long quantityPerDay;
		
	

	private long minPurchaseQuantity;
	

	private long maxPurchaseQuantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}


	public String getStrengthUnits() {
		return strengthUnits;
	}

	public void setStrengthUnits(String strengthUnits) {
		this.strengthUnits = strengthUnits;
	}

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getInsertedBy() {
		return insertedBy;
	}

	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public long getSaleUnits() {
		return saleUnits;
	}

	public void setSaleUnits(long saleUnits) {
		this.saleUnits = saleUnits;
	}

	public long getQuantityPerDay() {
		return quantityPerDay;
	}

	public void setQuantityPerDay(long quantityPerDay) {
		this.quantityPerDay = quantityPerDay;
	}

	public long getMinPurchaseQuantity() {
		return minPurchaseQuantity;
	}

	public void setMinPurchaseQuantity(long minPurchaseQuantity) {
		this.minPurchaseQuantity = minPurchaseQuantity;
	}

	public long getMaxPurchaseQuantity() {
		return maxPurchaseQuantity;
	}

	public void setMaxPurchaseQuantity(long maxPurchaseQuantity) {
		this.maxPurchaseQuantity = maxPurchaseQuantity;
	}

	public String getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(String itemLevel) {
		this.itemLevel = itemLevel;
	}

	public String getVendorPackage() {
		return vendorPackage;
	}

	public void setVendorPackage(String vendorPackage) {
		this.vendorPackage = vendorPackage;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}
	
	


}