package com.example.test.testingHMS.pharmacist.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;

@Entity
@Cacheable
@Table(name = "v_vendors_d")
public class Vendors implements Serializable {	
	
	@Id
	@Column(name = " vendor_id")
	private String vendorId;

	@Column(name = " vendor_type")
	private String vendorType;
	
	@Column(name = " business_type")
	private String businessType;
	
	@Column(name = " vendor_name")
	private String vendorName;
	
	
	@Column(name = " reg_no")
	private String regNo;
	
	@Column(name = " reg_name")
	private String regName;

	@Column(name = " pan_no")
	private String panNo;
	
	@Column(name = " gst_no")
	private String gstNo;
	
	@Column(name = " delivery_days")
	private long deliveryDays;
	
	@Column(name = " payment_terms")
	private String paymentTerms;
	
	@Column(name = " registered_date")
	private Timestamp registeredDate;
	
	@Column(name = " suppliers")
	private String suppliers;
	
	@Column(name = " adress1")
	private String adress1;
	
	@Column(name = " adress2")
	private String adress2;
	
	@Column(name = " area")
	private String area;
	
	@Column(name = " city")
	private String city;
	
	@Column(name = " state")
	private String state;
	
	@Column(name = " country")
	private String country;
	
	@Column(name = "pin_code")
	private long pinCode;
	
	@Column(name = "contact_person")
	private String contactPerson;
	
	@Column(name = "mobile")
	private long mobile;
	
	@Column(name = "fax")
	private String fax;
	
	@Column(name = "mail")
	private String mail;
	
	@Column(name = " bankName")
	private String bankName;
	
	@Column(name = "bankBranch")
	private String bankBranch;
	
	@Column(name = "  branch_adress")
	private String branchAdress;
	
	@Column(name = "  account_no")
	private String accountNo;
	
	@Column(name = " account_type")
	private String accountType;
	
	@Column(name = " ifsc_code")
	private String ifscCode;
	
	@Column(name = " micr_code")
	private String micrCode;
	
	@Column(name = " beneficiary_bank_name")
	private String beneficiaryBankName;

	@Column(name="active")
	private String active;
	
	@Column(name="inserted_by")
	private String insertedBy;
	
	@Column(name="inserted_date")
	private Timestamp insertedDate;
	
	@Column(name="modified_date")
	private Timestamp modifiedDate;
	
	@Column(name="modified_by")
	private String modeifiedBy;
	
	@OneToMany(mappedBy="medicineProcurmentVendors",cascade=CascadeType.ALL)
	private List<MedicineProcurement> medicineProcurement;
	


	@ManyToOne
	@JoinColumn(name="user_Id")
	private User vendorUser;
	
	
	public String getVendorId() {
		return vendorId;
	}


	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}


	public String getVendorType() {
		return vendorType;
	}


	public void setVendorType(String vendorType) {
		this.vendorType = vendorType;
	}


	public String getBusinessType() {
		return businessType;
	}


	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}


	public String getVendorName() {
		return vendorName;
	}


	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}


	public String getRegNo() {
		return regNo;
	}


	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}


	public String getRegName() {
		return regName;
	}


	public void setRegName(String regName) {
		this.regName = regName;
	}


	public String getPanNo() {
		return panNo;
	}


	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}


	public String getGstNo() {
		return gstNo;
	}


	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}


	public long getDeliveryDays() {
		return deliveryDays;
	}


	public void setDeliveryDays(long deliveryDays) {
		this.deliveryDays = deliveryDays;
	}


	public String getPaymentTerms() {
		return paymentTerms;
	}


	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}


	public Timestamp getRegisteredDate() {
		return registeredDate;
	}


	public void setRegisteredDate(Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}


	public String getSuppliers() {
		return suppliers;
	}


	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}


	public String getAdress1() {
		return adress1;
	}


	public void setAdress1(String adress1) {
		this.adress1 = adress1;
	}


	public String getAdress2() {
		return adress2;
	}


	public void setAdress2(String adress2) {
		this.adress2 = adress2;
	}


	public String getArea() {
		return area;
	}


	public void setArea(String area) {
		this.area = area;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public long getPinCode() {
		return pinCode;
	}


	public void setPinCode(long pinCode) {
		this.pinCode = pinCode;
	}


	public String getContactPerson() {
		return contactPerson;
	}


	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}


	public long getMobile() {
		return mobile;
	}


	public void setMobile(long mobile) {
		this.mobile = mobile;
	}


	public String getFax() {
		return fax;
	}


	public void setFax(String fax) {
		this.fax = fax;
	}


	public String getMail() {
		return mail;
	}


	public void setMail(String mail) {
		this.mail = mail;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getBankBranch() {
		return bankBranch;
	}


	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}


	public String getBranchAdress() {
		return branchAdress;
	}


	public void setBranchAdress(String branchAdress) {
		this.branchAdress = branchAdress;
	}


	public String getAccountNo() {
		return accountNo;
	}


	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}


	public String getAccountType() {
		return accountType;
	}


	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}


	public String getIfscCode() {
		return ifscCode;
	}


	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}


	public String getMicrCode() {
		return micrCode;
	}


	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}


	public String getBeneficiaryBankName() {
		return beneficiaryBankName;
	}


	public void setBeneficiaryBankName(String beneficiaryBankName) {
		this.beneficiaryBankName = beneficiaryBankName;
	}


	public String getActive() {
		return active;
	}


	public void setActive(String active) {
		this.active = active;
	}


	public String getInsertedBy() {
		return insertedBy;
	}


	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
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


	public String getModeifiedBy() {
		return modeifiedBy;
	}


	public void setModeifiedBy(String modeifiedBy) {
		this.modeifiedBy = modeifiedBy;
	}


	public List<MedicineProcurement> getMedicineProcurement() {
		return medicineProcurement;
	}


	public void setMedicineProcurement(List<MedicineProcurement> medicineProcurement) {
		this.medicineProcurement = medicineProcurement;
	}




	public User getVendorUser() {
		return vendorUser;
	}


	public void setVendorUser(User vendorUser) {
		this.vendorUser = vendorUser;
	}
	
	
	
}
	
