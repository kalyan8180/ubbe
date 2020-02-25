package com.example.test.testingHMS.appointment.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.appointment.model.SlotTiming;
import com.example.test.testingHMS.appointment.repository.SlotTimingRepository;
import com.example.test.testingHMS.appointment.service.SlotTimingService;

@Service
public class SlotTimingServiceImpl implements SlotTimingService{

	@Autowired
	SlotTimingRepository slotTimingRepository;
	
	@Override
	public List<SlotTiming> findBySlot(String slot) {
		
		return slotTimingRepository.findBySlot(slot);
	}

	@Override
	public SlotTiming findByFromTimeAndToTime(String fromTime, String toTime) {
		
		return slotTimingRepository.findByFromTimeAndToTime(fromTime, toTime);
	}

}