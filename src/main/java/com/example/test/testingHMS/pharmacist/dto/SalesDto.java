package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSales;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.TaxDetails;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SalesDto {
	private String saleNo;
	
	private String billNo;

	private String name;
	
	private String paymentType;
	
	private String paid;
	
	private float actualAmount;
	
	private long mobileNo;
	
	private Timestamp updatedDate;
	
	private String updatedBy;
	
	private float amount;
	
	private float costPrice;
	
	private Timestamp billDate;
	
	private long quantity;
	
	private String expireDate;
	
	private String referenceNumber;
	
	private String medicineName;

	private float mrp;
	
	private String shopName;
	
	private String soldBy;

	private String batchNo;
	
	private String umr;
	
	private float gst;
	
	private float discount;
	
	private String amtInWords;
	
	private transient List<RefSales> refSales;
	
	private transient String ward;
	
	private transient String regId;
	
	private transient List<Map<String, String>> addMedDetails;
	
	private transient String empId;
	
	private transient String location;
	
	private transient List<Map<String,String>> multimode;
	
	private transient String date;
	
	private transient List<MultiplePayment> multiplePayment;
	
	private List<ChargeBill> chargeBill;
	
	private MedicineDetails patientSalesMedicineDetails;

	
	private PatientRegistration patientRegistration;

	private Location patientSaleslocation;
	
	private User employeeId;

	private User patientSalesUser;
	
	private String ipSettledFlag;
	
	private String authorisedBy;


	
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	
	public Timestamp getBillDate() {
		return billDate;
	}

	public void setBillDate(Timestamp billDate) {
		this.billDate = billDate;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

		public String getSoldBy() {
		return soldBy;
	}

	public void setSoldBy(String soldBy) {
		this.soldBy = soldBy;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

		
	public String getAmtInWords() {
		return amtInWords;
	}

	public void setAmtInWords(String amtInWords) {
		this.amtInWords = amtInWords;
	}

	public MedicineDetails getPatientSalesMedicineDetails() {
		return patientSalesMedicineDetails;
	}

	public void setPatientSalesMedicineDetails(MedicineDetails patientSalesMedicineDetails) {
		this.patientSalesMedicineDetails = patientSalesMedicineDetails;
	}

	public PatientRegistration getPatientRegistration() {
		return patientRegistration;
	}

	public void setPatientRegistration(PatientRegistration patientRegistration) {
		this.patientRegistration = patientRegistration;
	}

	public Location getPatientSaleslocation() {
		return patientSaleslocation;
	}

	public void setPatientSaleslocation(Location patientSaleslocation) {
		this.patientSaleslocation = patientSaleslocation;
	}

	public User getPatientSalesUser() {
		return patientSalesUser;
	}

	public void setPatientSalesUser(User patientSalesUser) {
		this.patientSalesUser = patientSalesUser;
	}

	public List<RefSales> getRefSales() {
		return refSales;
	}

	public void setRefSales(List<RefSales> refSales) {
		this.refSales = refSales;
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

	public String getSaleNo() {
		return saleNo;
	}

	public void setSaleNo(String saleNo) {
		this.saleNo = saleNo;
	}

	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public List<ChargeBill> getChargeBill() {
		return chargeBill;
	}

	public void setChargeBill(List<ChargeBill> chargeBill) {
		this.chargeBill = chargeBill;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(float costPrice) {
		this.costPrice = costPrice;
	}

	public float getMrp() {
		return mrp;
	}

	public void setMrp(float mrp) {
		this.mrp = mrp;
	}

	public float getGst() {
		return gst;
	}

	public void setGst(float gst) {
		this.gst = gst;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public List<Map<String, String>> getMultimode() {
		return multimode;
	}

	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}

	public User getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(User employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public List<Map<String, String>> getAddMedDetails() {
		return addMedDetails;
	}

	public void setAddMedDetails(List<Map<String, String>> addMedDetails) {
		this.addMedDetails = addMedDetails;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public float getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(float actualAmount) {
		this.actualAmount = actualAmount;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getIpSettledFlag() {
		return ipSettledFlag;
	}

	public void setIpSettledFlag(String ipSettledFlag) {
		this.ipSettledFlag = ipSettledFlag;
	}

	public List<MultiplePayment> getMultiplePayment() {
		return multiplePayment;
	}

	public void setMultiplePayment(List<MultiplePayment> multiplePayment) {
		this.multiplePayment = multiplePayment;
	}

	public String getAuthorisedBy() {
		return authorisedBy;
	}

	public void setAuthorisedBy(String authorisedBy) {
		this.authorisedBy = authorisedBy;
	}


	
}
