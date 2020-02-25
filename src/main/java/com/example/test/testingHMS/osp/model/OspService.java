package com.example.test.testingHMS.osp.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.laboratory.helper.RefLaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.LabServices;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_ospservice_f")
public class OspService {
	
		@Id
		@Column(name = "master_osp_service_id")
		private String masterOspServiceId;
		
		@Column(name = "patient_name")
		private String patientName;
		
		@Column(name = "osp_service_id")
		private String OspServiceId;
		
		@Column(name = "paid")
		private String paid;
		
		@Column(name="reference_number")
		private String referenceNumber;
		
		@Column(name="dob")
		private Timestamp dob;
		
		@Column(name="quantity")
		 private float quantity;
		
		@Column(name="age")
		private String age;
		
		@Column(name = "updated_by")
		private String updatedBy;
		
		@Column(name = "updated_date")
		private Timestamp updatedDate;
		
		
		@Column(name = "mobile")
		private long mobile;
		
		@Column(name = "lab_service_date")
		private Timestamp labServiceDate;
		
		@Column(name = "reffered_by_id")
		private String refferedById;// user_id for doctor
		
		@Column(name = "service_name")
		private String serviceName;
		
		@Column(name="bill_no")
		private String billNo;


		
		@Column(name = "price")
		private float price;
		
		@Column(name = "discount")
		private float discount;
		
		@Column(name = "net_amount")
		private float netAmount;
		
		@Column(name = "entered_by")
		private String enteredBy;
		
		@Column(name = "modified_by")
		private String modifiedBy;
		
		@Column(name="gender")
		private String gender;
		
		@Column(name = "entered_date")
		private Timestamp enteredDate;
		
		@Column(name = "modified_date")
		private Timestamp modifiedDate;
		
		@Column(name = "status")
		private String status;
		
		@Column(name="paymentType")
		private String paymentType;
		
		@Column(name="authorised_by")
		private String authorisedBy;
		
		@Column(name="comments")
		private String comments;

		
		private transient String reg_id;
		
		private transient String pType;
		
		private transient List<MultiplePayment> multiplePayment;
		

		private transient List<Map<String,String>> multimode;
		
		private transient String docName;
		
		private transient String servName;
		
		private transient List<RefLaboratoryRegistration> refLaboratoryRegistrations;
		
		

		@JsonIgnore
		@ManyToOne
		@JoinColumn(name="service_id")
		private LabServices ospLabServices;
		
		

		@JsonIgnore
		@ManyToOne
		@JoinColumn(name="user_id")
		private User userOspService;






		public String getAge() {
			return age;
		}



		public void setAge(String age) {
			this.age = age;
		}



		public String getMasterOspServiceId() {
			return masterOspServiceId;
		}



		public String getGender() {
			return gender;
		}



		public void setGender(String gender) {
			this.gender = gender;
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



		public String getPatientName() {
			return patientName;
		}



		public void setPatientName(String patientName) {
			this.patientName = patientName;
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



		public String getReg_id() {
			return reg_id;
		}



		public void setReg_id(String reg_id) {
			this.reg_id = reg_id;
		}



		public String getpType() {
			return pType;
		}



		public void setpType(String pType) {
			this.pType = pType;
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