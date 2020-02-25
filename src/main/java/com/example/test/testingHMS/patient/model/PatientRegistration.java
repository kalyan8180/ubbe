package com.example.test.testingHMS.patient.model;

import java.io.Serializable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.ambulance.model.AmbulancePatientDetails;
import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.MasterCheckUpRegistration;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.patient.Helper.MultiplePayment;
import com.example.test.testingHMS.pharmacist.model.Sales;
import com.example.test.testingHMS.pharmacist.model.SalesRefund;
import com.example.test.testingHMS.pharmacist.model.SalesReturn;
import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name=" v_patient_registration_f")
public class PatientRegistration implements Serializable{
	@Id
	@Column(name="p_reg_id")
	private String regId;
	
	@Column(name="app_no")
	private String appNo;
	
	@Column(name="blocked_status")
	private boolean blockedStatus;
	
	@Column(name="revisits")
	private int revisits;
	
	@Column(name="reg_date")
	private Timestamp regDate;
	
	@Column(name="triggered_date")
	private Timestamp triggeredDate;
	
	@Column(name="reg_validity")
	private String regValidity;
	
	@Column(name="p_type")
	private String pType;
	
	@Column(name="procudure_name")
	private String procedureName;
	

	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="advance_amount")
	private long advanceAmount;
	
	@Column(name="estimation_amount")
	private long estimationAmount;
	
	@Column(name="cancellation_flag")
	private String cancellationFlag;
	
	@Column(name="reason_cancellation")
	private String reasonForCanncellation;
	
	@Column(name="insurance_status")
	private String insuranceStatus;
	
	@Column(name="insurance_company")
	private String insuranceCompany;
	
	
	private transient String rePatientType;
	
	private transient String referenceNumber;
	
	private transient Map<String,String> refName;

	private transient List<Map<String,String>> multimode;
	
	private transient String reConsultant;
	
	@Column(name="created_at")
	private Timestamp createdAt;
	
	@Column(name="deleted_at")
	private Timestamp deletedAt;
	
	@Column(name="joining_date")
	private Timestamp dateOfJoining;
	
	private transient List<MultiplePayment> multiplePayment;
	
	@JsonIgnore
	@OneToMany(mappedBy="patRegId",cascade=CascadeType.ALL)
	private List<ChargeBill> chargeBill;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id")
	private User vuserD;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "p_id")
	private PatientDetails patientDetails;
	
	@JsonIgnore
	@OneToMany(mappedBy="patientRegistrationCashCard",cascade=CascadeType.ALL)
	private List<CashPlusCard> cashPlusCard;
	
	

	@JsonIgnore
	@ManyToMany(cascade={ CascadeType.ALL })
	@JoinTable(joinColumns=@JoinColumn(name="p_reg_id"),
	inverseJoinColumns=@JoinColumn(name="pat_ambulance_id")
			)
	private List<AmbulancePatientDetails> ambulancePatientDetail;
	
	
	@JsonIgnore
	@OneToMany(mappedBy="patientRegistration",cascade=CascadeType.ALL)
	private List<Sales> sales;
	
	
	@OneToMany(mappedBy="checkuppatientRegistration",cascade=CascadeType.ALL)
	List<MasterCheckUpRegistration> masterCheckUpRegistration;
	
	@OneToMany(mappedBy="patientRegistrationBooking",cascade=CascadeType.ALL)
	private List<RoomBookingDetails> roomBookingDetails ;
	
	@JsonIgnore
	@OneToMany(mappedBy="salesReturnPatientRegistration",cascade=CascadeType.ALL)
	private List<SalesReturn> salesReturns;
	
	@JsonIgnore	
	@OneToMany(mappedBy="salesRefundPatientRegistration",cascade=CascadeType.ALL)
	private List<SalesRefund> salesRefunds;
	
	 
	@JsonIgnore	
	@OneToMany(mappedBy="laboratoryPatientRegistration",cascade=CascadeType.ALL)
	private List<LaboratoryRegistration> laboratoryRegistration;
	
	
	/*
	 * @JsonIgnore
	 * 
	 * @OneToMany(mappedBy="patientSalesPatientRegistration",cascade=CascadeType.
	 * ALL) private List<PatientSales> patientSales;
	 */
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "p_type_id")
	private PatientTypes patientType;
	
	@JsonIgnore
	@OneToMany(mappedBy="patientRegistrationNotes",cascade=CascadeType.ALL)
	private List<NotesDetails> notesDetails;
	
	
//	@JsonIgnore
	@OneToMany(mappedBy="patientRegistration",cascade= CascadeType.ALL,fetch = FetchType.EAGER)
	private Set<PatientPayment> patientPayment;

	

	
	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getAppNo() {
		return appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}

	public User getVuserD() {
		return vuserD;
	}

	public void setVuserD(User vuserD) {
		this.vuserD = vuserD;
	}

	

	

	public Timestamp getRegDate() {
		return regDate;
	}

	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}

	

	public PatientTypes getPatientType() {
		return patientType;
	}

	public void setPatientType(PatientTypes patientType) {
		this.patientType = patientType;
	}

	public String getRegValidity() {
		return regValidity;
	}

	public void setRegValidity(String regValidity) {
		this.regValidity = regValidity;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Timestamp getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Timestamp dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	
	public PatientTypes getpatientType() {
		return patientType;
	}

	public void setpatientType(PatientTypes patientType) {
		this.patientType = patientType;
	}

	public Set<PatientPayment> getPatientPayment() {
		return patientPayment;
	}

	public void setPatientPayment(Set<PatientPayment> patientPayment) {
		this.patientPayment = patientPayment;
	}

	public String getRePatientType() {
		return rePatientType;
	}

	public void setRePatientType(String rePatientType) {
		this.rePatientType = rePatientType;
	}

	public String getReConsultant() {
		return reConsultant;
	}

	public void setReConsultant(String reConsultant) {
		this.reConsultant = reConsultant;
	}

	public List<Sales> getSales() {
		return sales;
	}

	public void setSales(List<Sales> sales) {
		this.sales = sales;
	}

	public List<SalesReturn> getSalesReturns() {
		return salesReturns;
	}

	public void setSalesReturns(List<SalesReturn> salesReturns) {
		this.salesReturns = salesReturns;
	}

	public List<SalesRefund> getSalesRefunds() {
		return salesRefunds;
	}

	public void setSalesRefunds(List<SalesRefund> salesRefunds) {
		this.salesRefunds = salesRefunds;
	}

	public PatientDetails getPatientDetails() {
		return patientDetails;
	}

	public void setPatientDetails(PatientDetails patientDetails) {
		this.patientDetails = patientDetails;
	}

	/*
	 * public List<PatientSales> getPatientSales() { return patientSales; }
	 * 
	 * public void setPatientSales(List<PatientSales> patientSales) {
	 * this.patientSales = patientSales; }
	 */
	public List<LaboratoryRegistration> getLaboratoryRegistration() {
		return laboratoryRegistration;
	}

	public void setLaboratoryRegistration(List<LaboratoryRegistration> laboratoryRegistration) {
		this.laboratoryRegistration = laboratoryRegistration;
	}

	public List<ChargeBill> getChargeBill() {
		return chargeBill;
	}

	public void setChargeBill(List<ChargeBill> chargeBill) {
		this.chargeBill = chargeBill;
	}

	public List<NotesDetails> getNotesDetails() {
		return notesDetails;
	}

	public void setNotesDetails(List<NotesDetails> notesDetails) {
		this.notesDetails = notesDetails;
	}

	public List<RoomBookingDetails> getRoomBookingDetails() {
		return roomBookingDetails;
	}

	public void setRoomBookingDetails(List<RoomBookingDetails> roomBookingDetails) {
		this.roomBookingDetails = roomBookingDetails;
	}

	public long getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(long advanceAmount) {
		this.advanceAmount = advanceAmount;
	}

	public long getEstimationAmount() {
		return estimationAmount;
	}

	public void setEstimationAmount(long estimationAmount) {
		this.estimationAmount = estimationAmount;
	}

	public List<AmbulancePatientDetails> getAmbulancePatientDetail() {
		return ambulancePatientDetail;
	}

	public void setAmbulancePatientDetail(List<AmbulancePatientDetails> ambulancePatientDetail) {
		this.ambulancePatientDetail = ambulancePatientDetail;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String, String> getRefName() {
		return refName;
	}

	public void setRefName(Map<String, String> refName) {
		this.refName = refName;
	}

	public Timestamp getTriggeredDate() {
		return triggeredDate;
	}

	public void setTriggeredDate(Timestamp triggeredDate) {
		this.triggeredDate = triggeredDate;
	}

	public List<Map<String, String>> getMultimode() {
		return multimode;
	}

	public void setMultimode(List<Map<String, String>> multimode) {
		this.multimode = multimode;
	}

	public List<CashPlusCard> getCashPlusCard() {
		return cashPlusCard;
	}

	public void setCashPlusCard(List<CashPlusCard> cashPlusCard) {
		this.cashPlusCard = cashPlusCard;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public boolean isBlockedStatus() {
		return blockedStatus;
	}

	public void setBlockedStatus(boolean blockedStatus) {
		this.blockedStatus = blockedStatus;
	}

	public int getRevisits() {
		return revisits;
	}

	public void setRevisits(int revisits) {
		this.revisits = revisits;
	}

	public List<MultiplePayment> getMultiplePayment() {
		return multiplePayment;
	}

	public void setMultiplePayment(List<MultiplePayment> multiplePayment) {
		this.multiplePayment = multiplePayment;
	}

	public String getCancellationFlag() {
		return cancellationFlag;
	}

	public void setCancellationFlag(String cancellationFlag) {
		this.cancellationFlag = cancellationFlag;
	}

	public String getReasonForCanncellation() {
		return reasonForCanncellation;
	}

	public void setReasonForCanncellation(String reasonForCanncellation) {
		this.reasonForCanncellation = reasonForCanncellation;
	}

	public String getInsuranceStatus() {
		return insuranceStatus;
	}

	public void setInsuranceStatus(String insuranceStatus) {
		this.insuranceStatus = insuranceStatus;
	}

	public String getInsuranceCompany() {
		return insuranceCompany;
	}

	public void setInsuranceCompany(String insuranceCompany) {
		this.insuranceCompany = insuranceCompany;
	}

	public List<MasterCheckUpRegistration> getMasterCheckUpRegistration() {
		return masterCheckUpRegistration;
	}

	public void setMasterCheckUpRegistration(List<MasterCheckUpRegistration> masterCheckUpRegistration) {
		this.masterCheckUpRegistration = masterCheckUpRegistration;
	}


	
	
}
