package com.example.test.testingHMS.pharmacist.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.pharmacist.helper.RefMedicine;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Cacheable
@Table(name = "v_medicine_d")
public class MedicineDetails implements Serializable {
	
	

	@Id
	@Column(name="medicine_id" )
	public String medicineId;

	@Column(name="medicine_Name" )
	private String name;
	
	@Column(name="item_level" )
	private String itemLevel;
	
	@Column(name="batch_no" )
	private String batchNo;
	
	@Column(name="manufacturer" )
	private String manufacturer;
	
	@Column(name="vendor_package" )
	private String vendorPackage;
	
	@Column(name="brand" )
	private String brand;
	
	@Column(name="drug_type" )
	private String drugType;
	
	@Column(name = "strength_units")
	private String strengthUnits;
	
	@Column(name = "inserted_date")
	private Timestamp insertedDate;
	
	@Column(name = "modified_date")
	private Timestamp modifiedDate;
	
	@Column(name = "inserted_by")
	private String insertedBy;
	
	@Column(name = "modified_by")
	private String modifiedBy;
	
	@Column(name = "sale_Units")
	private long saleUnits;
	
	@Column(name = "quantity_PerDay")
	private long quantityPerDay;
		
	
	@Column(name = "min_Purchase_Quantity")
	private long minPurchaseQuantity;
	
	@Column(name = "max_Purchase_Quantity")
	private long maxPurchaseQuantity;

	
	private transient List<RefMedicine> refMedicine;
	
	
	@JsonIgnore
	@OneToMany(mappedBy="medicineProcurmentMedicineDetails",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	private List<MedicineProcurement> medicineProcurement;
	
	
	/*
	 * @JsonIgnore
	 * 
	 * @OneToMany(mappedBy="patientSalesMedicineDetails",cascade=CascadeType.ALL)
	 * private List<PatientSales> patientSales;
	 */
	@JsonIgnore
	@OneToMany(mappedBy="patientSalesMedicineDetails",cascade=CascadeType.ALL)
	private List<Sales> sales;
	
	
	

	public List<MedicineProcurement> getMedicineProcurement() {
		return medicineProcurement;
	}
	public void setMedicineProcurement(List<MedicineProcurement> medicineProcurement) {
		this.medicineProcurement = medicineProcurement;
	}

	/*
	 * public List<PatientSales> getPatientSales() { return patientSales; } public
	 * void setPatientSales(List<PatientSales> patientSales) { this.patientSales =
	 * patientSales; }
	 */
	public List<Sales> getSales() {
		return sales;
	}
	public void setSales(List<Sales> sales) {
		this.sales = sales;
	}

	public String getMedicineId() {
		return medicineId;
	}
	public void setMedicineId(String medicineId) {
		this.medicineId = medicineId;
	}
	public String getItemLevel() {
		return itemLevel;
	}
	public void setItemLevel(String itemLevel) {
		this.itemLevel = itemLevel;
	}
		public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
		public String getVendorPackage() {
		return vendorPackage;
	}
	public void setVendorPackage(String vendorPackage) {
		this.vendorPackage = vendorPackage;
	}
			public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDrugType() {
		return drugType;
	}
	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}
		public String getStrengthUnits() {
		return strengthUnits;
	}
	public void setStrengthUnits(String strengthUnits) {
		this.strengthUnits = strengthUnits;
	}
		public long getSaleUnits() {
		return saleUnits;
	}
	public void setSaleUnits(long saleUnits) {
		this.saleUnits = saleUnits;
	}
	public long getQuantityPerDay() {
		return quantityPerDay;
	}
	public void setQuantityPerDay(long quantityPerDay) {
		this.quantityPerDay = quantityPerDay;
	}
	
	
	
	public long getMinPurchaseQuantity() {
		return minPurchaseQuantity;
	}
	public void setMinPurchaseQuantity(long minPurchaseQuantity) {
		this.minPurchaseQuantity = minPurchaseQuantity;
	}
	public long getMaxPurchaseQuantity() {
		return maxPurchaseQuantity;
	}
	public void setMaxPurchaseQuantity(long maxPurchaseQuantity) {
		this.maxPurchaseQuantity = maxPurchaseQuantity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getInsertedBy() {
		return insertedBy;
	}
	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public List<RefMedicine> getRefMedicine() {
		return refMedicine;
	}
	public void setRefMedicine(List<RefMedicine> refMedicine) {
		this.refMedicine = refMedicine;
	}
	
	
	
}
