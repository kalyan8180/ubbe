package com.example.test.testingHMS.user.dto;

import com.example.test.testingHMS.user.model.User;


public class PasswordStuffDTO {
	
	private String passwordId ;
	
	private String password;
	
	private String confirmPassword;
	

	
	private String hintQuestion1;
	
	private String hintAnswer1;

	private String hintQuestion2;
	
	private String hintAnswer2;
	
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
