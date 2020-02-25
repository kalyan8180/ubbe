package com.example.test.testingHMS.laboratory.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

@Entity
@Table(name="v_nurse_laboratory_f")
public class NurseLaboratory {
	@Id
	@Column(name="nurse_id")
	private String nurseId;
	
	@Column(name="nurse_name")
	private String nurseName;
	
	@Column(name="date")
	private Timestamp date;
	
	@Column(name="patient_name")
	private String patientName;
	
	@Column(name="consultant")
	private String consultant;
	
	@Column(name="prescription")
	private String prescription;
	
	@Column(name="report")
	private String report;
	
	@Column(name="notes")
	private String notes;
	
	@Column(name="write_notes")
	private String writeNotes;
	
	@Column(name="entered_by")
	private String enteredBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="entered_date")
	private Timestamp enteredDate;
	
	@Column(name="modified_date")
	private Timestamp modifiedDate;
	
	
	@ManyToOne
	@JoinColumn(name="lab_service_id")
	private LaboratoryRegistration laboratoryRegistrationNurse;
	
	@ManyToOne
	@JoinColumn(name="nurse_raise_id")
	private RaisePharmacy raisePharmacyNurse;

	@ManyToOne
	@JoinColumn(name="note_id")
	private NotesDetails nurseLaboratoryNotes;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userNurseLaboratory;
	
	@ManyToOne
	@JoinColumn(name="p_reg_id")
	private PatientRegistration patientRegistrationLaboratory;

	public String getNurseId() {
		return nurseId;
	}

	public void setNurseId(String nurseId) {
		this.nurseId = nurseId;
	}

	public String getNurseName() {
		return nurseName;
	}

	public void setNurseName(String nurseName) {
		this.nurseName = nurseName;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getConsultant() {
		return consultant;
	}

	public void setConsultant(String consultant) {
		this.consultant = consultant;
	}

	
	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getWriteNotes() {
		return writeNotes;
	}

	public void setWriteNotes(String writeNotes) {
		this.writeNotes = writeNotes;
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

	

	public LaboratoryRegistration getLaboratoryRegistrationNurse() {
		return laboratoryRegistrationNurse;
	}

	public void setLaboratoryRegistrationNurse(LaboratoryRegistration laboratoryRegistrationNurse) {
		this.laboratoryRegistrationNurse = laboratoryRegistrationNurse;
	}

	public RaisePharmacy getRaisePharmacyNurse() {
		return raisePharmacyNurse;
	}

	public void setRaisePharmacyNurse(RaisePharmacy raisePharmacyNurse) {
		this.raisePharmacyNurse = raisePharmacyNurse;
	}


	public NotesDetails getNurseLaboratoryNotes() {
		return nurseLaboratoryNotes;
	}

	public void setNurseLaboratoryNotes(NotesDetails nurseLaboratoryNotes) {
		this.nurseLaboratoryNotes = nurseLaboratoryNotes;
	}

	public User getUserNurseLaboratory() {
		return userNurseLaboratory;
	}

	public void setUserNurseLaboratory(User userNurseLaboratory) {
		this.userNurseLaboratory = userNurseLaboratory;
	}

	public PatientRegistration getPatientRegistrationLaboratory() {
		return patientRegistrationLaboratory;
	}

	public void setPatientRegistrationLaboratory(PatientRegistration patientRegistrationLaboratory) {
		this.patientRegistrationLaboratory = patientRegistrationLaboratory;
	}
	
	
}