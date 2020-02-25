package com.example.test.testingHMS.appointment.service;

import java.util.List;

import com.example.test.testingHMS.appointment.model.SlotTiming;

public interface SlotTimingService {
	public List<SlotTiming> findBySlot(String slot);
	public SlotTiming findByFromTimeAndToTime(String fromTime,String toTime);

}