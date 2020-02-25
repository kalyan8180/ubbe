package com.example.test.testingHMS.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.appointment.dto.DoctorAppointmentDto;
import com.example.test.testingHMS.appointment.model.DoctorAppointment;
import com.example.test.testingHMS.appointment.model.SlotTiming;
import com.example.test.testingHMS.appointment.repository.DoctorAppointmentRepository;
import com.example.test.testingHMS.appointment.repository.SlotTimingRepository;
import com.example.test.testingHMS.appointment.serviceImpl.DoctorAppoinmentServiceImpl;
import com.example.test.testingHMS.appointment.serviceImpl.SlotTimingServiceImpl;
import com.example.test.testingHMS.user.model.DoctorDetails;
import com.example.test.testingHMS.user.model.User;
import com.example.test.testingHMS.user.serviceImpl.DoctorDetailsServiceImpl;
import com.example.test.testingHMS.user.serviceImpl.UserServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/appointment")

public class AppointmentController {
	
	public static Logger Logger=LoggerFactory.getLogger(AppointmentController.class);
	
	
	@Autowired
	DoctorAppoinmentServiceImpl doctorAppoinmentServiceImpl;
	
	@Autowired
	DoctorAppointmentRepository doctorAppointmentRepository;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	SlotTimingServiceImpl slotTimingServiceImpl;
	
	
	@Autowired
	SlotTimingRepository slotTimingRepository;
	
	@Autowired
	DoctorDetailsServiceImpl doctorDetailsServiceImpl;
	
	
	@RequestMapping(value="/getdoctor/{specilization}")
	public List<DoctorDetails> findTheDoctor(@PathVariable("specilization") String specilization){
		
		return doctorDetailsServiceImpl.findBySpecilization(specilization);
	}
	
	
	@RequestMapping(value="/getslots/{slot}")
	public List<SlotTiming> findBySlot(@PathVariable("slot") String slot){
		
		return slotTimingServiceImpl.findBySlot(slot);
	}
	
	@RequestMapping(value="/create/{slot}")
	public List<Object> getPageLoad(@PathVariable("slot") String slot){
		
		
		return doctorAppoinmentServiceImpl.pageLoad(slot);
		
	}
	@RequestMapping(value="/create")
	public List<Object> getPageRefresh(){
		
		return doctorAppoinmentServiceImpl.pageRefresh();
	}
	
	
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public void computeSave(@RequestBody DoctorAppointmentDto doctorAppointmentDto,Principal principal){
		
		DoctorAppointment doctorAppointment=new DoctorAppointment();
		BeanUtils.copyProperties(doctorAppointmentDto, doctorAppointment);
		
		doctorAppoinmentServiceImpl.computeSave(doctorAppointment,principal);
	}
	
	@RequestMapping(value="/getAll")
		public List<DoctorAppointment> getAll(){
			
			return doctorAppoinmentServiceImpl.findAll();
		}
		
		@RequestMapping(value="/getappointments",method=RequestMethod.POST)
		public List<Object> getAppointments(@RequestBody Map<String , String> appointMent) throws ParseException{
			
			
			
			String doctorName=appointMent.get("doctorName");
			
			String shift=appointMent.get("shift");
			String date=appointMent.get("appointmentDate").substring(0, 10);
			Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(date);
			
			/*
			Timestamp date1 = sales.getBillDate();
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date1.getTime());
			salesDate = format1.format(cal.getTime());*/
			
			Timestamp date2=Timestamp.valueOf(appointMent.get("appointmentDate").substring(0, 10)+" "+appointMent.get("appointmentDate").substring(11, 19));
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date2.getTime());
			String salesDate = format1.format(cal.getTime());
			//getNoAppointments(doctorName, shift, date);
			//List<DoctorAppointment> appontmentList=doctorAppointmentRepository.findByDoctorNameAndShiftAndAppiointmentDate(doctorName,shift,date);//getAllAppiontments(doctorName, shift, date);
			List<DoctorAppointment> appontmentList=doctorAppointmentRepository.getNewAppointments(doctorName, shift, date);
			List<SlotTiming> appontmentList1=slotTimingRepository.getnonAppointments(doctorName,shift,date);
			
			
			List<Object> app=new ArrayList<>();
			
			Map<String, String> map=new HashMap<>();
			map.put("doctorName", doctorName);
			map.put("shift", shift);
			map.put("date", date);
			app.add(map);
			List<Object> list=new ArrayList<>(appontmentList);
			
			list.addAll(appontmentList1);
			
			//list.add(app);
			
			
			List<Object> newlist=new ArrayList<>();
			newlist.add(list);
			newlist.add(app);
			return newlist;
		}
		
		
		
	}