package com.example.test.testingHMS.bill.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.bill.helper.RefBillDetails;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ChargeBillDto
{
	private String chargeBillId;
	
	private String billNo;
	
	private float amount;
	
	private float discount;
	
	private long quantity;
	
	private float netAmount;
	
	private Timestamp updatedDate;
	
	private String updatedBy;
	
	private float mrp;
	
	private String paymentType;
	
	private String insertedBy;
	
	private Timestamp insertedDate;
	
	private Timestamp dichargedDate;
	
	private String paid;
	
	private String ipSettledFlag;
	
	private transient List<Map<String,String>> updateCharge;
	
	
	private transient String referenceNumber;
	
	private transient List<RefBillDetails> refBillDetails; 
	
	private transient List<Map<String,String>> multimode;
	
	private transient float returnAmount;
	
	private transient String regId;
	
	private transient String procedure;
	
	private transient String type;
	
	private transient List<MultiplePayment> multiplePayment;
	
	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	private transient String serviceName;

	private PatientRegistration patRegId;
	
	
	private Sales saleId;
	
	private LaboratoryRegistration labId;
	
	private User userChargeBillId;

	public String getChargeBillId() {
		return chargeBillId;
	}

	public void setChargeBillId(String chargeBillId) {
		this.chargeBillId = chargeBillId;
	}

	

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	
	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(float netAmount) {
		this.netAmount = netAmount;
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

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public PatientRegistration getPatRegId() {
		return patRegId;
	}

	public void setPatRegId(PatientRegistration patRegId) {
		this.patRegId = patRegId;
	}

	

	public Sales getSaleId() {
		return saleId;
	}

	public void setSaleId(Sales saleId) {
		this.saleId = saleId;
	}

	public User getUserChargeBillId() {
		return userChargeBillId;
	}

	public void setUserChargeBillId(User userChargeBillId) {
		this.userChargeBillId = userChargeBillId;
	}

	

	public List<RefBillDetails> getRefBillDetails() {
		return refBillDetails;
	}

	public void setRefBillDetails(List<RefBillDetails> refBillDetails) {
		this.refBillDetails = refBillDetails;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public LaboratoryRegistration getLabId() {
		return labId;
	}

	public void setLabId(LaboratoryRegistration labId) {
		this.labId = labId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public Timestamp getDichargedDate() {
		return dichargedDate;
	}

	public void setDichargedDate(Timestamp dichargedDate) {
		this.dichargedDate = dichargedDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public float getMrp() {
		return mrp;
	}

	public void setMrp(float mrp) {
		this.mrp = mrp;
	}

	public List<Map<String, String>> getMultimode() {
		return multimode;
	}

	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}

	public List<Map<String, String>> getUpdateCharge() {
		return updateCharge;
	}

	public void setUpdateCharge(List<Map<String, String>> updateCharge) {
		this.updateCharge = updateCharge;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public float getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(float returnAmount) {
		this.returnAmount = returnAmount;
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

	public String getIpSettledFlag() {
		return ipSettledFlag;
	}

	public void setIpSettledFlag(String ipSettledFlag) {
		this.ipSettledFlag = ipSettledFlag;
	}

	public List<MultiplePayment> getMultiplePayment() {
		return multiplePayment;
	}

	public void setMultiplePayment(List<MultiplePayment> multiplePayment) {
		this.multiplePayment = multiplePayment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
	

	
}
