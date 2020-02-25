package com.example.test.testingHMS.pharmacist.model;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;
@Entity
@Table(name="v_location_d")
public class Location implements Serializable{
	@Id
	@Column(name="location_id" )	
	private String locationId;
	
	@Column(name="location_name" )
	private String locationName;
	
	@Column(name="adress1" )
	private String adress1;
	
	@Column(name="adress2" )
	private String adress2;
	
	@Column(name="city" )
	private String city ; 
	
	@Column(name="state" )
	private String state;
	
	@Column(name="pincode" )
	private String pincode; 
	
	@Column(name="contact_no1" )
	private long contactNo1;
	
	@Column(name="contact_no2" )
	private long contactNo2;
	
	@Column(name="creted_by" )
	private String cretedBy;
	
	@Column(name="created_date" )
	private Timestamp createdDate;
	
	
	/*
	 * @OneToMany(mappedBy="patientSaleslocation",cascade=CascadeType.ALL) private
	 * List<PatientSales> patientSales;
	 */	
	@OneToMany(mappedBy="patientSaleslocation",cascade=CascadeType.ALL)
	private List<Sales> sales;

	@OneToMany(mappedBy="medicineProcurmentLocation",cascade=CascadeType.ALL)
	private List<MedicineProcurement> medicineProcurement;
	

	@OneToMany(mappedBy="vendorInvoiceLocation",cascade=CascadeType.ALL)
	private List<VendorsInvoice> vendorsInvoice;
	
	//patientcheckUplocation
	

	@OneToMany(mappedBy="patientcheckUplocation",cascade=CascadeType.ALL)
	List<MasterCheckUpRegistration> masterCheckUpRegistration;
	
	
	
	public List<Sales> getSales() {
		return sales;
	}
	public void setSales(List<Sales> sales) {
		this.sales = sales;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
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
	
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public List<MedicineProcurement> getMedicineProcurement() {
		return medicineProcurement;
	}
	public void setMedicineProcurement(List<MedicineProcurement> medicineProcurement) {
		this.medicineProcurement = medicineProcurement;
	}
	public List<VendorsInvoice> getVendorsInvoice() {
		return vendorsInvoice;
	}
	public void setVendorsInvoice(List<VendorsInvoice> vendorsInvoice) {
		this.vendorsInvoice = vendorsInvoice;
	}
	public long getContactNo1() {
		return contactNo1;
	}
	public void setContactNo1(long contactNo1) {
		this.contactNo1 = contactNo1;
	}
	public long getContactNo2() {
		return contactNo2;
	}
	public void setContactNo2(long contactNo2) {
		this.contactNo2 = contactNo2;
	}
	public String getCretedBy() {
		return cretedBy;
	}
	public void setCretedBy(String cretedBy) {
		this.cretedBy = cretedBy;
	}
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	public List<MasterCheckUpRegistration> getMasterCheckUpRegistration() {
		return masterCheckUpRegistration;
	}
	public void setMasterCheckUpRegistration(List<MasterCheckUpRegistration> masterCheckUpRegistration) {
		this.masterCheckUpRegistration = masterCheckUpRegistration;
	} 
	
	

}
