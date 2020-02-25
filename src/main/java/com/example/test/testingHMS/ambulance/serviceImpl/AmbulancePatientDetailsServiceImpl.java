package com.example.test.testingHMS.ambulance.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.ambulance.model.AmbulancePatientDetails;
import com.example.test.testingHMS.ambulance.model.AmbulanceServices;
import com.example.test.testingHMS.ambulance.repository.AmbulancePatientDetailsRepository;
import com.example.test.testingHMS.ambulance.repository.AmbulanceServicesRepository;
import com.example.test.testingHMS.ambulance.service.AmbulancePatientDetailsService;
import com.example.test.testingHMS.controller.BillController;

@Service
public class AmbulancePatientDetailsServiceImpl implements AmbulancePatientDetailsService {

	public static Logger Logger=LoggerFactory.getLogger(AmbulancePatientDetailsServiceImpl.class);
	
	@Autowired
	AmbulancePatientDetailsRepository ambulancePatientDetailsRepository;

	@Autowired
	AmbulancePatientDetailsServiceImpl ambulancePatientDetailsServiceImpl;

	@Autowired
	AmbulanceServicesRepository ambulanceServicesRepository;


	public String getNextLabId() {
		AmbulancePatientDetails ambulancePatientDetails = ambulancePatientDetailsRepository
				.findFirstByOrderByPatAmbulanceIdDesc();

		String nextId = null;
		if (ambulancePatientDetails == null) {
			nextId = "APD0000001";
		} else {
			int nextIntId = Integer.parseInt(ambulancePatientDetails.getPatAmbulanceId().substring(3));
			nextIntId += 1;
			nextId = "APD" + String.format("%07d", nextIntId);
		}
		return nextId;
	}

	@Override
	public List<Object> pageLoad() {

		List<Object> a1 = new ArrayList<>();
		Map<String,String> info=new HashMap<>();
		info.put("nextAmbId", ambulancePatientDetailsServiceImpl.getNextLabId());

		List<AmbulanceServices> ambulanceServices = ambulanceServicesRepository.getAmbulanceStatus();
		a1.add(ambulanceServices);
		a1.add(info);
		return a1;

	}

	@Override
	public void update(AmbulancePatientDetails ambulancePatientDetails) {

		AmbulancePatientDetails ambulancePatientDetailsInfo = ambulancePatientDetailsRepository
				.findByPatAmbulanceId(ambulancePatientDetails.getPatAmbulanceId());

		ambulancePatientDetails.setAmbulanceNo(ambulancePatientDetailsInfo.getAmbulanceNo());
		ambulancePatientDetails.setPatName(ambulancePatientDetailsInfo.getPatName());
		ambulancePatientDetails.setMobileNo(ambulancePatientDetailsInfo.getMobileNo());
		ambulancePatientDetails.setFromLocation(ambulancePatientDetailsInfo.getFromLocation());
		ambulancePatientDetails.setToLocation(ambulancePatientDetailsInfo.getToLocation());
		ambulancePatientDetails.setFromTime(ambulancePatientDetailsInfo.getFromTime());
		ambulancePatientDetails.setDriverName(ambulancePatientDetailsInfo.getDriverName());
		ambulancePatientDetails.setToTime(Timestamp.valueOf(LocalDateTime.now()));
		
		AmbulanceServices ambulanceServices = ambulanceServicesRepository
				.findByAmbulanceNO(ambulancePatientDetails.getAmbulanceNo());

		ambulanceServices.setStatus(1);

		ambulanceServicesRepository.save(ambulanceServices);
		
		ambulancePatientDetails.setAmbulanceServices(ambulanceServices);
		ambulancePatientDetailsRepository.save(ambulancePatientDetails);

		
	}

	public void computeSave(AmbulancePatientDetails ambulancePatientDetails) {
		AmbulanceServices ambulanceServices=new AmbulanceServices();
		ambulancePatientDetails.setFromTime(Timestamp.valueOf(LocalDateTime.now()));
		
		
		 ambulanceServices = ambulanceServicesRepository
				.findByAmbulanceNO(ambulancePatientDetails.getAmbulanceNo());

		ambulanceServices.setStatus(0);
		
		ambulancePatientDetails.setAmbulanceServices(ambulanceServices);
		ambulancePatientDetailsRepository.save(ambulancePatientDetails);
	}

	@Override
	public List<AmbulancePatientDetails> findAll() {

		List<AmbulancePatientDetails> ambulancePatientDetails = ambulancePatientDetailsRepository.findAll();
		return ambulancePatientDetails;
	}

}
