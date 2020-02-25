package com.example.test.testingHMS.pharmacist.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.pharmacist.helper.RefMedicineDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Cacheable
@Table(name = "v_medicine_procurement_d")
public class MedicineProcurement implements Serializable{
	@Id
	@Column(name = "master_procurement_id")
	private String masterProcurementId;
	
	@Column(name = "procurement_id")
	private String procurementId;
	
	@Column(name = "draft")
	private String draft;
	
	@Column(name = "invoiceNo")
	private String invoiceNo;
	
	@Column(name = "cost_price")
	private float costPrice;
	
	@Column(name="mrp")
	private float mrp;
	
	@Column(name = "date_of_procurement")
	private Timestamp dateOfProcurement;
	
	@Column(name="po_no")
    private String poNo;
	
	@Column(name="paid")
    private String paid;
	
	@Column(name="inserted_date")
	private Timestamp insertedDate;
	
	@Column(name = "modified_date")
	private Timestamp modifiedDate;
	
	
	
	@Column(name = "quantity")
	private float quantity;
	
	@Column(name = "detailed_quantity")
	private long detailedQuantity;
	
	
	@Column(name = "pack_size")
	private long packSize;
	
	
	@Column(name="free_sample")
	private String freeSample;

	@Column(name="gst")
	private float gst;
	
	@Column(name="discount")
	private float discount;
	
	@Column(name = "item_Name")
	private String itemName;
	
	@Column(name = "batch")
	private String batch;
	
	@Column(name="procurement_type")
	private String procurementType;
	
	@Column(name="status")
	private String status;
	
	@Column(name="currency")
	private String currency;
	
	@Column(name="amount")
	private float amount;
	
	@Column(name="manufactured_date")
	private String manufacturedDate;
	

	@Column(name = "packing")
	private String packing;

	@Column(name = "exp_Date")
	private String expDate;
	
	@Column(name = "tax")
	private float tax;
	
	


	private transient String vendorName;
	
	private transient String medName;
	
	private transient String dateOfProc;
	
	private transient String location;
	
	private transient String role;
	
	
	private transient List<RefMedicineDetails> refMedicineDetails;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private  Vendors medicineProcurmentVendors;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location  medicineProcurmentLocation;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "medicine_id")
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

	public String getDateOfProc() {
		return dateOfProc;
	}

	public void setDateOfProc(String dateOfProc) {
		this.dateOfProc = dateOfProc;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}
