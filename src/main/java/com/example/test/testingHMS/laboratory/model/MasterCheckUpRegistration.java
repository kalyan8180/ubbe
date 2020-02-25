package com.example.test.testingHMS.laboratory.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.laboratory.helper.MasterCheckUpHelper;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "v_master_checkup_registration_f")
public class MasterCheckUpRegistration {

	@Id
	@Column(name = "checkup_registration_id")
	private String checkUpRegistrationId;

	@Column(name = "bill_no")
	private String billNo;

	@Column(name = "patient_name")
	private String patientName;

	@Column(name = "payment_type")
	private String paymentType;

	@Column(name = "paid")
	private String paid;

	@Column(name = "mobile_no")
	private long mobileNo;

	@Column(name = "checkup_name")
	private String checkupName;

	@Column(name = "price")
	private float price;

	@Column(name = "final_amt")
	private float finalAmount;

	@Column(name = "bill_date")
	private Timestamp billDate;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "umr")
	private String umr;

	private transient String regId;

	private transient String location;

	private transient List<MultiplePayment> multiplePayment;
	
	private transient List<MasterCheckUpHelper> masterCheckUpHelper;
	
	private transient float  discount;
	private transient float  totalAmount;
	private transient float  netAmount;
	

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "p_reg_id")
	private PatientRegistration checkuppatientRegistration;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location patientcheckUplocation;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_Id")
	private User checkUpRegistrationUser;

	@ManyToOne
	@JoinColumn(name = "checkup_id")
	private MasterCheckupService masterCheckupService;

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

	public User getCheckUpRegistrationUser() {
		return checkUpRegistrationUser;
	}

	public void setCheckUpRegistrationUser(User checkUpRegistrationUser) {
		this.checkUpRegistrationUser = checkUpRegistrationUser;
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
