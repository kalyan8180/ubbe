package com.example.test.testingHMS.bed.service;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.patient.model.PatientRegistration;

public interface RoomBookingDetailsService {

	String getNextBookingId();
	
	RoomBookingDetails getroomStatus(String id);
	
	void save(RoomBookingDetails roomBookingDetails);
	
	RoomBookingDetails findByPatientRegistrationBooking(PatientRegistration patientRegistration);
	
	RoomBookingDetails findByPatientRegistrationBookingAndStatus(PatientRegistration patientRegistration,int status);

	


}
