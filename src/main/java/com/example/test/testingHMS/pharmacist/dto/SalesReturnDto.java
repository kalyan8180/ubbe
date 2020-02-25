package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSalesReturn;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.user.model.User;


public class SalesReturnDto {

	private String saleNo;
	
	private String masterSaleNo;
	
	private String billNo;
	
	private String shopName;
	
	private Timestamp date;
	
	private String umrNo;
	
	private long mobileNo;
	
	private String name;
	
	private String medicineName;
	
	private float amount;

	private String batchNo;
	
	private String paymentType;
	
	private float mrp;
	
	private long quantity;
	
	private float discount;
	
	private float gst;
	
	private String raisedBy;
	
	private transient List<RefSalesReturn> refSalesReturns;

	private transient String regId;
	
	private transient String location;

	private PatientRegistration salesReturnPatientRegistration;

	private Location salesReturnLocation;

	private User salesReturnUser;

	public PatientRegistration getSalesReturnPatientRegistration() {
		return salesReturnPatientRegistration;
	}

	public void setSalesReturnPatientRegistration(PatientRegistration salesReturnPatientRegistration) {
		this.salesReturnPatientRegistration = salesReturnPatientRegistration;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getUmrNo() {
		return umrNo;
	}

	public void setUmrNo(String umrNo) {
		this.umrNo = umrNo;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

	

	

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getMrp() {
		return mrp;
	}

	public void setMrp(float mrp) {
		this.mrp = mrp;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getGst() {
		return gst;
	}

	public void setGst(float gst) {
		this.gst = gst;
	}

	public String getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(String raisedBy) {
		this.raisedBy = raisedBy;
	}


	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}


	public Location getSalesReturnLocation() {
		return salesReturnLocation;
	}

	public void setSalesReturnLocation(Location salesReturnLocation) {
		this.salesReturnLocation = salesReturnLocation;
	}

	public User getSalesReturnUser() {
		return salesReturnUser;
	}

	public void setSalesReturnUser(User salesReturnUser) {
		this.salesReturnUser = salesReturnUser;
	}

	public String getSaleNo() {
		return saleNo;
	}

	public void setSaleNo(String saleNo) {
		this.saleNo = saleNo;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public String getMasterSaleNo() {
		return masterSaleNo;
	}

	public void setMasterSaleNo(String masterSaleNo) {
		this.masterSaleNo = masterSaleNo;
	}

	public List<RefSalesReturn> getRefSalesReturns() {
		return refSalesReturns;
	}

	public void setRefSalesReturns(List<RefSalesReturn> refSalesReturns) {
		this.refSalesReturns = refSalesReturns;
	}

	

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
