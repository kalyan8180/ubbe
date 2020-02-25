package com.example.test.testingHMS.pharmacist.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="v_medicine_quantity")
public class MedicineQuantity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="inventory_id")
	private long inventoryId;
	
	@Column(name="batch_id")
	private String batchId;
	
	@Column(name="total_Quantity")
	private float totalQuantity;
	
	@Column(name="total_sold")
	private long sold;
	
	@Column(name="remaining_balance")
	private float balance;
	
	@Column(name="med_name")
	private String medName;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="medicine_id")
	private MedicineDetails medicineDetails;

	public long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(long inventoryId) {
		this.inventoryId = inventoryId;
	}

	
	public long getSold() {
		return sold;
	}

	public void setSold(long sold) {
		this.sold = sold;
	}


	public float getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(float totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public MedicineDetails getMedicineDetails() {
		return medicineDetails;
	}

	public void setMedicineDetails(MedicineDetails medicineDetails) {
		this.medicineDetails = medicineDetails;
	}

	public String getMedName() {
		return medName;
	}

	public void setMedName(String medName) {
		this.medName = medName;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	

}
