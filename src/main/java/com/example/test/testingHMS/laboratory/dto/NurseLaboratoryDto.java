package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.test.testingHMS.laboratory.model.LaboratoryRegistration;
import com.example.test.testingHMS.laboratory.model.NotesDetails;
import com.example.test.testingHMS.laboratory.model.RaisePharmacy;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

public class NurseLaboratoryDto {
	private String nurseId;
	
	private String nurseName;
	
	private Timestamp date;
	
	private String patientName;
	
	private String consultant;
	
	private String prescription;
	
	private String report;
	
	private String notes;
	
	private String writeNotes;
	
	private String enteredBy;
	
	private String modifiedBy;
	
	private Timestamp enteredDate;
	
	private Timestamp modifiedDate;
	
	
	private LaboratoryRegistration laboratoryRegistrationNurse;
	
	private RaisePharmacy raisePharmacyNurse;

	private NotesDetails nurseLaboratoryNotes;
	
	private User userNurseLaboratory;
	
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