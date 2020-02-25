package com.example.test.testingHMS.bill.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.bill.helper.RefBillDetails;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_charge_bill_f")
public class ChargeBill
{
	@Id
	@Column(name="charge_bill_id")
	private String chargeBillId;
	
	private String billNo;
	
	@Column(name="amount")
	private float amount;
	
	@Column(name="Email")
	private String email;
	
	@Column(name="discount")
	private float discount;
	
	@Column(name="mrp")
	private float mrp;
	
	@Column(name="quantity")
	private long quantity;
	
	@Column(name="netAmount")
	private float netAmount;
	
	@Column(name="inserted_by")
	private String insertedBy;
	
	@Column(name="paymentType")
	private String paymentType;
	
	@Column(name="inserted_date")
	private Timestamp insertedDate;
	
	@Column(name="updated_date")
	private Timestamp updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	
	@Column(name="dicharged_date")
	private Timestamp dichargedDate;
	
	@Column(name="paid")
	private String paid;
	
	@Column(name="ip_settled_flag")
	private String ipSettledFlag;
	
	private transient List<Map<String,String>> updateCharge;
	
	private transient List<Map<String,String>> multimode;
	
	private transient String referenceNumber;
	
	private transient List<RefBillDetails> refBillDetails; 
	
	private transient String regId;
	
	private transient String serviceName;
	
	private transient String procedure;
	
	private transient float returnAmount;
	
	private transient String type;
	
	private transient List<MultiplePayment> multiplePayment;

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="pat_reg_id")
	private PatientRegistration patRegId;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="service_id")
	private LabServices serviceId;

	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="sale_id")
	private Sales saleId;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="lab_id")
	private LaboratoryRegistration labId;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id")
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

	public LabServices getServiceId() {
		return serviceId;
	}

	public void setServiceId(LabServices serviceId) {
		this.serviceId = serviceId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
