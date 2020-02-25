package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.example.test.testingHMS.laboratory.model.NurseLaboratory;
import com.example.test.testingHMS.patient.model.PatientRegistration;
import com.example.test.testingHMS.user.model.User;

public class NotesDetailsDto {
	
	private String noteId;
		
	private String notes;
	
	private String writeNotes;
	
	private Timestamp insertedDate;
	
	private String insertedBy;
	
	private String modifiedBy;
	
	private Timestamp modifiedDate;
	
	private User userNotes;
	
	private PatientRegistration patientRegistrationNotes;
	
	private List<NurseLaboratory> nurseLaboratory;
	
	private transient String regId;

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
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

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public String getInsertedBy() {
		return insertedBy;
	}

	public void setInsertedBy(String insertedBy) {
		this.insertedBy = insertedBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public User getUserNotes() {
		return userNotes;
	}

	public void setUserNotes(User userNotes) {
		this.userNotes = userNotes;
	}

	public PatientRegistration getPatientRegistrationNotes() {
		return patientRegistrationNotes;
	}

	public void setPatientRegistrationNotes(PatientRegistration patientRegistrationNotes) {
		this.patientRegistrationNotes = patientRegistrationNotes;
	}

	public List<NurseLaboratory> getNurseLaboratory() {
		return nurseLaboratory;
	}

	public void setNurseLaboratory(List<NurseLaboratory> nurseLaboratory) {
		this.nurseLaboratory = nurseLaboratory;
	}

	
	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	
	
}
	
	
	
	
	
