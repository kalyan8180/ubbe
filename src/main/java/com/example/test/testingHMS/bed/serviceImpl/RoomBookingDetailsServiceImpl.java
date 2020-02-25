package com.example.test.testingHMS.bed.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.repository.RoomBookingDetailsRepository;
import com.example.test.testingHMS.bed.service.RoomBookingDetailsService;
import com.example.test.testingHMS.patient.model.PatientRegistration;

@Service
public class RoomBookingDetailsServiceImpl implements RoomBookingDetailsService{
	
	public static Logger Logger=LoggerFactory.getLogger(RoomBookingDetailsServiceImpl.class);
	
	
	@Autowired
	RoomBookingDetailsRepository roomBookingDetailsRepository;
	
	@Override
	public String getNextBookingId() {
		RoomBookingDetails roomBookingDetails= roomBookingDetailsRepository.findFirstByOrderByBookingIdDesc();
		String nextId=null;
		if(roomBookingDetails==null)
		{
			nextId="RB0000001";
		}
		else
		{
		int nextIntId=Integer.parseInt(roomBookingDetails.getBookingId().substring(2));
		nextIntId+=1;
		nextId="RB"+String.format("%07d", nextIntId);
		}
		return nextId;
	}
	
	public RoomBookingDetails getroomStatus(String id)
	{
		return roomBookingDetailsRepository.getroomStatus(id);
	}
	
	public void save(RoomBookingDetails roomBookingDetails)
	{
		roomBookingDetailsRepository.save(roomBookingDetails);
	}
	
	public RoomBookingDetails findByPatientRegistrationBooking(PatientRegistration patientRegistration)
	{
		return roomBookingDetailsRepository.findByPatientRegistrationBooking(patientRegistration);
	}
	
	public 	RoomBookingDetails findByPatientRegistrationBookingAndStatus(PatientRegistration patientRegistration,int status)
	{
		return roomBookingDetailsRepository.findByPatientRegistrationBookingAndStatus(patientRegistration, status);
	}
	
}
