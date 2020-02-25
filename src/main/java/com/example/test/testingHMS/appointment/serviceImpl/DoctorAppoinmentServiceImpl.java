package com.example.test.testingHMS.appointment.serviceImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.ambulance.serviceImpl.AmbulancePatientDetailsServiceImpl;
import com.example.test.testingHMS.appointment.model.DoctorAppointment;
import com.example.test.testingHMS.appointment.model.SlotTiming;
import com.example.test.testingHMS.appointment.repository.DoctorAppointmentRepository;
import com.example.test.testingHMS.appointment.service.DoctorAppoinmentService;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.repository.UserRepository;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;

@Service
public class DoctorAppoinmentServiceImpl implements DoctorAppoinmentService{
	
	public static Logger Logger=LoggerFactory.getLogger(DoctorAppoinmentServiceImpl.class);
	
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SlotTimingServiceImpl slotTimingServiceImpl;
	
	
	@Autowired
	DoctorAppointmentRepository doctorAppointmentRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	DoctorAppoinmentServiceImpl doctorAppoinmentServiceImpl;
	
	public String getNextAppointmentId() {
		DoctorAppointment doctorAppointment = doctorAppointmentRepository.findFirstByOrderByAppointmentIdDesc();

		String nextId = null;
		if (doctorAppointment == null) {
			nextId = "APT0000001";
		} else {
			int nextIntId = Integer.parseInt(doctorAppointment.getAppointmentId().substring(3));
			nextIntId += 1;
			nextId = "APT" + String.format("%07d", nextIntId);
		}
		return nextId;
	}
	

	@Override
	public List<Object> pageRefresh() {
		
		List<Object> list=new ArrayList<>();
		Map<String,String> info=new HashMap<>();
		info.put("nextAppointmentId",doctorAppoinmentServiceImpl.getNextAppointmentId() );
		list.add(info);
		return list;
	}

	

	@Override
	public List<Object> pageLoad(String slot) {
		
		List<Object> list=new ArrayList<>();
		Map<String,String> info=new HashMap<>();
		info.put("nextAppointmentId",doctorAppoinmentServiceImpl.getNextAppointmentId() );
		List<SlotTiming> slotTiming=slotTimingServiceImpl.findBySlot(slot);
		List<User> user=userServiceImpl.findByUserRole("DOCTOR");
		list.add(info);
		list.add(user);
		list.add(slotTiming);
		
		return list;
	}

	
	@Override
	public void computeSave(DoctorAppointment doctorAppointment,Principal principal) {
		
		SlotTiming slotTiming=new SlotTiming();
		
		
		// Security
		User userSecurity=userServiceImpl.findByUserName(principal.getName());
		String createdBy=userSecurity.getFirstName()+" "+userSecurity.getMiddleName()+" "+userSecurity.getLastName();

		doctorAppointment.setAppointmentId(getNextAppointmentId());
		
		slotTiming=slotTimingServiceImpl.findByFromTimeAndToTime(doctorAppointment.getFromTime(), doctorAppointment.getToTime());
		
		if(doctorAppointment.getShift().equalsIgnoreCase("MORNING SHIFT")){
			
			doctorAppointment.setShiftTime("8-12");
		}else if(doctorAppointment.getShift().equalsIgnoreCase("AFTERNOON SHIFT")){
			doctorAppointment.setShiftTime("12-4");
		}else if(doctorAppointment.getShift().equalsIgnoreCase("EVENING SHIFT")){
			doctorAppointment.setShiftTime("4-10");
		}
		doctorAppointment.setStatus("ALLOCATED");
		doctorAppointment.setSlotTiming(slotTiming);
		doctorAppointment.setCreatedBy(createdBy);
		doctorAppointment.setAppointmentUser(userSecurity);
		doctorAppointmentRepository.save(doctorAppointment);
		
		
	}

	@Override
	public List<DoctorAppointment> findAll() {
   return doctorAppointmentRepository.findAll();
	}

}