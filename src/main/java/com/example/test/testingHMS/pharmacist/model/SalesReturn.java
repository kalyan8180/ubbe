package com.example.test.testingHMS.pharmacist.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSalesReturn;
import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name = "v_sales_return")
public class SalesReturn {

	@Id
	@Column(name = "return_sale_no")
	private String saleNo;
	
	@Column(name = "master_sale_no")
	private String masterSaleNo;
	
	@Column(name = "bill_no")
	private String billNo;
	
	@Column(name = "shop_name")
	private String shopName;
	
	@Column(name = "date")
	private Timestamp date;
	
	@Column(name = "umr_no")
	private String umrNo;
	
	@Column(name = "mobile_no")
	private long mobileNo;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "medicine_name")
	private String medicineName;
	
	@Column(name = "amount")
	private float amount;

	@Column(name = "batch_no")
	private String batchNo;
	
	@Column(name = "payment_type")
	private String paymentType;
	

	@Column(name = "mrp")
	private float mrp;
	
	@Column(name = "quantity")
	private long quantity;
	
	@Column(name = "discount")
	private float discount;
	
	
	@Column(name = "gst")
	private float gst;
	
	@Column(name = "raised_by")
	private String raisedBy;
	
	private transient List<RefSalesReturn> refSalesReturns;

	private transient String regId;
	
	private transient String location;

	@ManyToOne
	@JoinColumn(name = "p_reg_id")
	private PatientRegistration salesReturnPatientRegistration;

	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location salesReturnLocation;

	@ManyToOne
	@JoinColumn(name = "user_Id")
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