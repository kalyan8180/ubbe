package com.example.test.testingHMS.patient.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "v_marketing_question_f")
public class MarketingQuestions implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="q_id")
	private Long qId;
	
	@Column(name="question")
	private String question;

	@OneToMany(mappedBy = "vMarketingQuestion")
	private List<PatientDetails> vPatientDetails;

	public Long getqId() {
		return qId;
	}

	public void setqId(Long qId) {
		this.qId = qId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<PatientDetails> getvPatientDetails() {
		return vPatientDetails;
	}

	public void setvPatientDetails(List<PatientDetails> vPatientDetails) {
		this.vPatientDetails = vPatientDetails;
	}

	

	
}
