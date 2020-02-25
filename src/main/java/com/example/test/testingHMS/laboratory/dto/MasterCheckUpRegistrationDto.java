package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;

import com.example.test.testingHMS.laboratory.helper.MasterCheckUpHelper;
import com.example.test.testingHMS.laboratory.model.MasterCheckupService;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.user.model.User;

public class MasterCheckUpRegistrationDto {

	private String checkUpRegistrationId;

	private String billNo;

	private String patientName;

	private String paymentType;

	private String paid;

	private long mobileNo;

	private String checkupName;

	private float price;

	private float finalAmount;

	private Timestamp billDate;

	private Timestamp createdDate;

	private String createdBy;

	private Timestamp updatedDate;

	private String updatedBy;

	private String referenceNumber;

	private String umr;

	private PatientRegistration checkuppatientRegistration;

	private Location patientcheckUplocation;

	private User checkUpRegistrationUser;

	private MasterCheckupService masterCheckupService;

	private transient String regId;

	private transient String location;
	private transient float  discount;
	private transient float  totalAmount;
	private transient float  netAmount;
	
	private transient List<MultiplePayment> multiplePayment;

	private transient List<MasterCheckUpHelper> masterCheckUpHelper;
	
	public String getCheckUpRegistrationId() {
		return checkUpRegistrationId;
	}

	public void setCheckUpRegistrationId(String checkUpRegistrationId) {
		this.checkUpRegistrationId = checkUpRegistrationId;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCheckupName() {
		return checkupName;
	}

	public void setCheckupName(String checkupName) {
		this.checkupName = checkupName;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(float finalAmount) {
		this.finalAmount = finalAmount;
	}

	public Timestamp getBillDate() {
		return billDate;
	}

	public void setBillDate(Timestamp billDate) {
		this.billDate = billDate;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
	}

	public PatientRegistration getCheckuppatientRegistration() {
		return checkuppatientRegistration;
	}

	public void setCheckuppatientRegistration(PatientRegistration checkuppatientRegistration) {
		this.checkuppatientRegistration = checkuppatientRegistration;
	}

	public Location getPatientcheckUplocation() {
		return patientcheckUplocation;
	}

	public void setPatientcheckUplocation(Location patientcheckUplocation) {
		this.patientcheckUplocation = patientcheckUplocation;
	}

	public User getCheckUpRegistrationUser() {
		return checkUpRegistrationUser;
	}

	public void setCheckUpRegistrationUser(User checkUpRegistrationUser) {
		this.checkUpRegistrationUser = checkUpRegistrationUser;
	}

	public MasterCheckupService getMasterCheckupService() {
		return masterCheckupService;
	}

	public void setMasterCheckupService(MasterCheckupService masterCheckupService) {
		this.masterCheckupService = masterCheckupService;
	}

	public List<MultiplePayment> getMultiplePayment() {
		return multiplePayment;
	}

	public void setMultiplePayment(List<MultiplePayment> multiplePayment) {
		this.multiplePayment = multiplePayment;
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

	public List<MasterCheckUpHelper> getMasterCheckUpHelper() {
		return masterCheckUpHelper;
	}

	public void setMasterCheckUpHelper(List<MasterCheckUpHelper> masterCheckUpHelper) {
		this.masterCheckUpHelper = masterCheckUpHelper;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public float getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(float netAmount) {
		this.netAmount = netAmount;
	}
	

}
