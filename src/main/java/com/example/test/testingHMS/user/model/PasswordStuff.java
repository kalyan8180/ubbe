package com.example.test.testingHMS.user.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "v_passwordstuff_f")
public class PasswordStuff implements Serializable{
	
	@Id
	/*@GenericGenerator(name = "sequence_password_id", strategy = "com.example.test.testingHMS.user.model.PasswordIdGenerator")
    @GeneratedValue(generator = "sequence_password_id")*/
	@Column(name = "passwordId")
	private String passwordId ;
		
	public PasswordStuff() {
		super();
	}

	@NotNull
	@Column(name = "password")
	private String password;

	@NotNull
	@Column(name = "confirm_password")
	private String confirmPassword;
	
	
	@Column(name = "hint_question1")
	private String hintQuestion1;
	
	@NotNull
	@Column(name = "hint_answer1")
	private String hintAnswer1;

	@Column(name = "hint_question2")
	private String hintQuestion2;
	
	@NotNull
	@Column(name = "hint_answer2")
	private String hintAnswer2;

	
	@JsonBackReference
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "userId")
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}


	public String getHintQuestion1() {
		return hintQuestion1;
	}

	public void setHintQuestion1(String hintQuestion1) {
		this.hintQuestion1 = hintQuestion1;
	}

	public String getHintAnswer1() {
		return hintAnswer1;
	}

	public void setHintAnswer1(String hintAnswer1) {
		this.hintAnswer1 = hintAnswer1;
	}

	public String getHintQuestion2() {
		return hintQuestion2;
	}

	public void setHintQuestion2(String hintQuestion2) {
		this.hintQuestion2 = hintQuestion2;
	}

	public String getHintAnswer2() {
		return hintAnswer2;
	}

	public void setHintAnswer2(String hintAnswer2) {
		this.hintAnswer2 = hintAnswer2;
	}

	public String getPasswordId() {
		return passwordId;
	}

	public void setPasswordId(String passwordId) {
		this.passwordId = passwordId;
	}

}
