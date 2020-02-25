package com.example.test.testingHMS.pharmacyShopDetails.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_pharmacy_shop_details_d")
public class PharmacyShopDetails {
	@Id
	@Column(name = "shop_id")
	private String shopId;
	
	@Column(name = "shop_location")
	private String shopLocation;
	
	@Column(name = "gst_no")
	private String gstNO;
	
	@Column(name = "dl_no")
	private String dlNo;
	
	@Column(name = "cin_no")
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