package com.example.test.testingHMS.pharmacist.helper;

import java.util.List;

import org.springframework.stereotype.Component;

/*
 * Reference for displaying list
 * of approved procurement
 */


@Component
public class RefApprovedListHelper 
{
	String procurementId;
	
	String procurementDate;
	
	long cost;
	
	private long balanceAmount;
	
	private long paid_amount;
	
	private long dueAmount;
	
	private List<String> invoice;

	public String getProcurementId() {
		return procurementId;
	}

	public void setProcurementId(String procurementId) {
		this.procurementId = procurementId;
	}

	
	
	public String getProcurementDate() {
		return procurementDate;
	}

	public void setProcurementDate(String procurementDate) {
		this.procurementDate = procurementDate;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}
	
	

	public long getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(long balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public long getPaid_amount() {
		return paid_amount;
	}

	public void setPaid_amount(long paid_amount) {
		this.paid_amount = paid_amount;
	}

	public long getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(long dueAmount) {
		this.dueAmount = dueAmount;
	}
	
	

	public List<String> getInvoice() {
		return invoice;
	}

	public void setInvoice(List<String> invoice) {
		this.invoice = invoice;
	}

	@Override
	public String toString() {
		return "RefApprovedListHelper [procurementId=" + procurementId + ", procurementDate=" + procurementDate
				+ ", cost=" + cost + "]";
	}
	
	
	
	
	
}
