	package com.example.test.testingHMS.pharmacist.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.patient.model.PatientDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.pharmacist.helper.RefSales;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="v_sales_f")
public class Sales {

	@Id
	@Column(name = "sale_no")
	private String saleNo;
	
	@Column(name = "bill_no")
	private String billNo;

	@Column(name = "name")
	private String name;
	
	@Column(name = "payment_type")
	private String paymentType;
	
	@Column(name = "paid")
	private String paid;
	
	@Column(name="mobile_no")
	private long mobileNo;
	
	@Column(name = "amount")
	private float amount;
	
	@Column(name = "actual_amount")
	private float actualAmount;
	
	@Column(name = "costPrice")
	private float costPrice;
	
	@Column(name = "bill_date")
	private Timestamp billDate;
	
	@Column(name = "updated_date")
	private Timestamp updatedDate;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "quantity")
	private long quantity;
	
	@Column(name="reference_number")
	private String referenceNumber;
	
	@Column(name = "expire_date")
	private String expireDate;
	
	@Column(name="medicine_Name" )
	private String medicineName;

	@Column(name="mrp")
	private float mrp;
	
	@Column(name = "shop_name")
	private String shopName;
	
	@Column(name = "sold_by")
	private String soldBy;

	@Column(name = "batch_no")
	private String batchNo;
	
	@Column(name = "umr")
	private String umr;
	
	@Column(name="gst")
	private float gst;
	
	@Column(name="discount")
	private float discount;
	
	@Column(name="amt_in_words")
	private String amtInWords;
	
	@Column(name="ip_settled_flag")
	private String ipSettledFlag;
	
	@Column(name="authorised_by")
	private String authorisedBy;
	
	private transient List<RefSales> refSales;
	
	private transient String regId;
	
	private transient String location;
	
	private transient String empId;
	
	private transient List<Map<String, String>> addMedDetails;
	
	private transient List<Map<String,String>> multimode;
	
	private transient String date;
	
	private transient List<MultiplePayment> multiplePayment;
	
	
	@OneToMany(mappedBy="saleId",cascade=CascadeType.ALL)
	private List<ChargeBill> chargeBill;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "medicine_id")
	private MedicineDetails patientSalesMedicineDetails;

	@ManyToOne
	@JoinColumn(name="employee_id")
	private User employeeId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "p_reg_id")
	private PatientRegistration patientRegistration;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location patientSaleslocation;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_Id")
	private User patientSalesUser;

	
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	
	public Timestamp getBillDate() {
		return billDate;
	}

	public void setBillDate(Timestamp billDate) {
		this.billDate = billDate;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

		public String getSoldBy() {
		return soldBy;
	}

	public void setSoldBy(String soldBy) {
		this.soldBy = soldBy;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

		
	public String getAmtInWords() {
		return amtInWords;
	}

	public void setAmtInWords(String amtInWords) {
		this.amtInWords = amtInWords;
	}

	public MedicineDetails getPatientSalesMedicineDetails() {
		return patientSalesMedicineDetails;
	}

	public void setPatientSalesMedicineDetails(MedicineDetails patientSalesMedicineDetails) {
		this.patientSalesMedicineDetails = patientSalesMedicineDetails;
	}

	public PatientRegistration getPatientRegistration() {
		return patientRegistration;
	}

	public void setPatientRegistration(PatientRegistration patientRegistration) {
		this.patientRegistration = patientRegistration;
	}

	public Location getPatientSaleslocation() {
		return patientSaleslocation;
	}

	public void setPatientSaleslocation(Location patientSaleslocation) {
		this.patientSaleslocation = patientSaleslocation;
	}

	public User getPatientSalesUser() {
		return patientSalesUser;
	}

	public void setPatientSalesUser(User patientSalesUser) {
		this.patientSalesUser = patientSalesUser;
	}

	public List<RefSales> getRefSales() {
		return refSales;
	}

	public void setRefSales(List<RefSales> refSales) {
		this.refSales = refSales;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSaleNo() {
		return saleNo;
	}

	public void setSaleNo(String saleNo) {
		this.saleNo = saleNo;
	}

	public String getUmr() {
		return umr;
	}

	public void setUmr(String umr) {
		this.umr = umr;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public List<ChargeBill> getChargeBill() {
		return chargeBill;
	}

	public void setChargeBill(List<ChargeBill> chargeBill) {
		this.chargeBill = chargeBill;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(float costPrice) {
		this.costPrice = costPrice;
	}

	public float getMrp() {
		return mrp;
	}

	public void setMrp(float mrp) {
		this.mrp = mrp;
	}

	public float getGst() {
		return gst;
	}

	public void setGst(float gst) {
		this.gst = gst;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public List<Map<String, String>> getMultimode() {
		return multimode;
	}

	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}

	public User getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(User employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public List<Map<String, String>> getAddMedDetails() {
		return addMedDetails;
	}

	public void setAddMedDetails(List<Map<String, String>> addMedDetails) {
		this.addMedDetails = addMedDetails;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public float getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(float actualAmount) {
		this.actualAmount = actualAmount;
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