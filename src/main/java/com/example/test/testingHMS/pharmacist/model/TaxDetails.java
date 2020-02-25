package com.example.test.testingHMS.pharmacist.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="v_tax_dts")
public class TaxDetails {
	@Id
	@Column(name="tax_id" )
	private String taxId;
	@Column(name="sgst" )
	private long sgst;
	@Column(name="cgst" )
	private long cgst;
	@Column(name="tgst" )
	private long tgst;
	
	
	
	/*
	 * @OneToMany(mappedBy="patientSalesTaxDetails",cascade=CascadeType.ALL) private
	 * List<PatientSales> patientSales;
	 * 
	 * 
	 * public List<PatientSales> getPatientSales() { return patientSales; } public
	 * void setPatientSales(List<PatientSales> patientSales) { this.patientSales =
	 * patientSales; }
	 */
	public String getTaxId() {
		return taxId;
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	public long getSgst() {
		return sgst;
	}
	public void setSgst(long sgst) {
		this.sgst = sgst;
	}
	public long getCgst() {
		return cgst;
	}
	public void setCgst(long cgst) {
		this.cgst = cgst;
	}
	public long getTgst() {
		return tgst;
	}
	public void setTgst(long tgst) {
		this.tgst = tgst;
	}
	
	
	


}
