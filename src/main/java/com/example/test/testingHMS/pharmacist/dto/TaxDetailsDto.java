package com.example.test.testingHMS.pharmacist.dto;

public class TaxDetailsDto {

	private String taxId;
	private long gst;
	private long cgst;
	private long tgst;
	public String getTaxId() {
		return taxId;
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	public long getGst() {
		return gst;
	}
	public void setGst(long gst) {
		this.gst = gst;
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
