package com.example.test.testingHMS.pharmacyShopDetails.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class PharmacyShopDetailsDto {
	private String shopId;
	
	private String shopLocation;
	
	private String gstNO;
	
	private String dlNo;
	
	private String cinNO;

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getShopLocation() {
		return shopLocation;
	}

	public void setShopLocation(String shopLocation) {
		this.shopLocation = shopLocation;
	}

	public String getGstNO() {
		return gstNO;
	}

	public void setGstNO(String gstNO) {
		this.gstNO = gstNO;
	}

	public String getDlNo() {
		return dlNo;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public String getCinNO() {
		return cinNO;
	}

	public void setCinNO(String cinNO) {
		this.cinNO = cinNO;
	}

}