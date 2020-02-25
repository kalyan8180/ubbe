package com.example.test.testingHMS.appointment.service;

import java.security.Principal;
import java.util.List;

import com.example.test.testingHMS.appointment.model.DoctorAppointment;

public interface DoctorAppoinmentService {
	
	public List<Object> pageLoad(String slot);
	
	public List<Object> pageRefresh();

	void computeSave(DoctorAppointment doctorAppointment,Principal principal);
	
	List<DoctorAppointment> findAll();

}