package com.example.test.testingHMS.ospDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.example.test.testingHMS.laboratory.helper.RefLaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.user.model.User;

public class OspServiceDto {
	
	private String masterOspServiceId;
	
	private String patientName;
	
	private String OspServiceId;
	
	private String paid;
	

	private String updatedBy;
	
	private Timestamp updatedDate;
	
	
	private String referenceNumber;
	
	private Timestamp dob;
	
	private long mobile;
	
	 private float quantity;
	
	private Timestamp labServiceDate;
	
	private String refferedById;// user_id for doctor
	
	private String serviceName;
	
	private float price;
	
	private String billNo;

	private transient List<Map<String,String>> multimode;
	
	private float discount;
	
	private float netAmount;
	
	private String age;
	
	private String enteredBy;
	
	private String modifiedBy;
	
	private Timestamp enteredDate;
	
	private Timestamp modifiedDate;
	
	private String status;
	
	private String paymentType;
	private String gender;
	
	private String authorisedBy;
	private String comments;

	
	private transient List<MultiplePayment> multiplePayment;
	
	
	private transient String docName;
	
	private transient String servName;
	
	private transient List<RefLaboratoryRegistration> refLaboratoryRegistrations;
	
	

	private LabServices ospLabServices;
	
	
	private User userOspService;


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getAge() {
		return age;
	}


	public void setAge(String age) {
		this.age = age;
	}


	public String getPatientName() {
		return patientName;
	}


	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}



	public String getMasterOspServiceId() {
		return masterOspServiceId;
	}


	public void setMasterOspServiceId(String masterOspServiceId) {
		this.masterOspServiceId = masterOspServiceId;
	}


	public String getOspServiceId() {
		return OspServiceId;
	}


	public void setOspServiceId(String ospServiceId) {
		OspServiceId = ospServiceId;
	}


	public String getPaid() {
		return paid;
	}


	public void setPaid(String paid) {
		this.paid = paid;
	}


	public Timestamp getDob() {
		return dob;
	}


	public void setDob(Timestamp dob) {
		this.dob = dob;
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


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getDocName() {
		return docName;
	}


	public void setDocName(String docName) {
		this.docName = docName;
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


	public LabServices getOspLabServices() {
		return ospLabServices;
	}


	public void setOspLabServices(LabServices ospLabServices) {
		this.ospLabServices = ospLabServices;
	}


	public User getUserOspService() {
		return userOspService;
	}


	public void setUserOspService(User userOspService) {
		this.userOspService = userOspService;
	}


	public String getBillNo() {
		return billNo;
	}


	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}


	public List<Map<String, String>> getMultimode() {
		return multimode;
	}


	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}


	public float getQuantity() {
		return quantity;
	}


	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}


	public String getReferenceNumber() {
		return referenceNumber;
	}


	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}


	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}


	public Timestamp getUpdatedDate() {
		return updatedDate;
	}


	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
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


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	

	
	

}