package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.pharmacist.helper.RefMedicineDetails;
import com.example.test.testingHMS.pharmacist.model.Location;
import com.example.test.testingHMS.pharmacist.model.MedicineDetails;
import com.example.test.testingHMS.pharmacist.model.TaxDetails;
import com.example.test.testingHMS.pharmacist.model.Vendors;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MedicineProcurementDto {
	private String masterProcurementId;
	
	private String procurementId;
	
	private float costPrice;
	
	private float mrp;
	
	private Timestamp dateOfProcurement;
	
    private String poNo;
    
	private String draft;
    
	private long packSize;
	
	private long detailedQuantity;
	
	private Timestamp insertedDate;
	
	private Timestamp modifiedDate;
	
	private float discount;
	
	private String paid;
	
	private float quantity;
	
	private String freeSample;
	
	private String itemName;
	
	private String batch;
	
	private String procurementType;
	
	private String status;
	
	private String currency;
	
	private float amount;
	
	private String manufacturedDate;
	

	private String packing;

	private String expDate;
	
	private float tax;
	
	private float gst;
	
	private transient String vendorName;
	
	private transient String medName;
	
	private transient String location;
	
	private transient String role;
	
	private String invoiceNo;
	
	private transient List<RefMedicineDetails> refMedicineDetails;
	
	private  Vendors medicineProcurmentVendors;
	
	private Location  medicineProcurmentLocation;

	private MedicineDetails  medicineProcurmentMedicineDetails;

	public String getMasterProcurementId() {
		return masterProcurementId;
	}

	public void setMasterProcurementId(String masterProcurementId) {
		this.masterProcurementId = masterProcurementId;
	}

	public String getProcurementId() {
		return procurementId;
	}

	public void setProcurementId(String procurementId) {
		this.procurementId = procurementId;
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

	public Timestamp getDateOfProcurement() {
		return dateOfProcurement;
	}

	public void setDateOfProcurement(Timestamp dateOfProcurement) {
		this.dateOfProcurement = dateOfProcurement;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
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

	
	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public String getFreeSample() {
		return freeSample;
	}

	public void setFreeSample(String freeSample) {
		this.freeSample = freeSample;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getProcurementType() {
		return procurementType;
	}

	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getManufacturedDate() {
		return manufacturedDate;
	}

	public void setManufacturedDate(String manufacturedDate) {
		this.manufacturedDate = manufacturedDate;
	}

	public String getPacking() {
		return packing;
	}

	public void setPacking(String packing) {
		this.packing = packing;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public float getTax() {
		return tax;
	}

	public void setTax(float tax) {
		this.tax = tax;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getMedName() {
		return medName;
	}

	public void setMedName(String medName) {
		this.medName = medName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public List<RefMedicineDetails> getRefMedicineDetails() {
		return refMedicineDetails;
	}

	public void setRefMedicineDetails(List<RefMedicineDetails> refMedicineDetails) {
		this.refMedicineDetails = refMedicineDetails;
	}

	public Vendors getMedicineProcurmentVendors() {
		return medicineProcurmentVendors;
	}

	public void setMedicineProcurmentVendors(Vendors medicineProcurmentVendors) {
		this.medicineProcurmentVendors = medicineProcurmentVendors;
	}

	public Location getMedicineProcurmentLocation() {
		return medicineProcurmentLocation;
	}

	public void setMedicineProcurmentLocation(Location medicineProcurmentLocation) {
		this.medicineProcurmentLocation = medicineProcurmentLocation;
	}

	public MedicineDetails getMedicineProcurmentMedicineDetails() {
		return medicineProcurmentMedicineDetails;
	}

	public void setMedicineProcurmentMedicineDetails(MedicineDetails medicineProcurmentMedicineDetails) {
		this.medicineProcurmentMedicineDetails = medicineProcurmentMedicineDetails;
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

	public long getPackSize() {
		return packSize;
	}

	public void setPackSize(long packSize) {
		this.packSize = packSize;
	}

	public long getDetailedQuantity() {
		return detailedQuantity;
	}

	public void setDetailedQuantity(long detailedQuantity) {
		this.detailedQuantity = detailedQuantity;
	}

	public String getDraft() {
		return draft;
	}

	public void setDraft(String draft) {
		this.draft = draft;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	
	
	

	
	

}
