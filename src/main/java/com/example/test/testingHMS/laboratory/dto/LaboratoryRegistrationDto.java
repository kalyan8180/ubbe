package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.laboratory.helper.RefLaboratoryRegistration;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

public class LaboratoryRegistrationDto {
	
	private String labRegId;
	
	private String patientName;
	
	private String invoiceNo;
	
	private String paid;
	
	private String billNo;
	
	private Timestamp updatedDate;
	
	private String updatedBy;
	
	private String paymentType;
	
	private long mobile;
	
	private String referenceNumber;
	
	private Timestamp labServiceDate;
	
	private String refferedById;// user_id for doctor
	
	private String serviceName;
	
	private float price;
	
	private float discount;
	
	private float netAmount;
	
	private String enteredBy;
	
	private String modifiedBy;
	
	private Timestamp enteredDate;
	
	private Timestamp modifiedDate;
	
	private String status;
	
	private long quantity;
	
	private String ipSettledFlag;
	
	private String authorisedBy;

	
	private User userLaboratoryRegistration;
	
	
	private PatientRegistration laboratoryPatientRegistration;

	private transient String reg_id;
	
	private transient String servName;
	
	private transient List<Map<String,String>> multimode;
	
	private transient List<MultiplePayment> multiplePayment;
	
	private transient List<RefLaboratoryRegistration> refLaboratoryRegistrations;

	public String getLabRegId() {
		return labRegId;
	}

	public void setLabRegId(String labRegId) {
		this.labRegId = labRegId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
		this.mobile = mobile;
	}

	public Timestamp getLabServiceDate() {
		return labServiceDate;
	}

	public void setLabServiceDate(Timestamp labServiceDate) {
		this.labServiceDate = labServiceDate;
	}

	public String getRefferedById() {
		return refferedById;
	}

	public void setRefferedById(String refferedById) {
		this.refferedById = refferedById;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
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

	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getEnteredDate() {
		return enteredDate;
	}

	public void setEnteredDate(Timestamp enteredDate) {
		this.enteredDate = enteredDate;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUserLaboratoryRegistration() {
		return userLaboratoryRegistration;
	}

	public void setUserLaboratoryRegistration(User userLaboratoryRegistration) {
		this.userLaboratoryRegistration = userLaboratoryRegistration;
	}

	public PatientRegistration getLaboratoryPatientRegistration() {
		return laboratoryPatientRegistration;
	}

	public void setLaboratoryPatientRegistration(PatientRegistration laboratoryPatientRegistration) {
		this.laboratoryPatientRegistration = laboratoryPatientRegistration;
	}

	public String getReg_id() {
		return reg_id;
	}

	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}

	public String getServName() {
		return servName;
	}

	public void setServName(String servName) {
		this.servName = servName;
	}

	public List<RefLaboratoryRegistration> getRefLaboratoryRegistrations() {
		return refLaboratoryRegistrations;
	}

	public void setRefLaboratoryRegistrations(List<RefLaboratoryRegistration> refLaboratoryRegistrations) {
		this.refLaboratoryRegistrations = refLaboratoryRegistrations;
	}

	public List<Map<String, String>> getMultimode() {
		return multimode;
	}

	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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

	public String getAuthorisedBy() {
		return authorisedBy;
	}

	public void setAuthorisedBy(String authorisedBy) {
		this.authorisedBy = authorisedBy;
	}

	
}
