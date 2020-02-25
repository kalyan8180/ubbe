package com.example.test.testingHMS.bed.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bed.repository.RoomDetailsRepository;
import com.example.test.testingHMS.bed.service.RoomDetailsService;

@Service
public class RoomDetailsServiceImpl implements RoomDetailsService{
	
	public static Logger Logger=LoggerFactory.getLogger(RoomDetailsServiceImpl.class);
	

	@Autowired
	RoomDetailsRepository roomDetailsRepository;

	public List<RoomDetails> findAll()
	{
		return roomDetailsRepository.findAll();
		
	}
	
	public List<RoomDetails> getRooms(String floor,String ward)
	{
		return roomDetailsRepository.getRooms(floor, ward);
	}
	
	public RoomDetails findByBedName(String name)
	{
		return roomDetailsRepository.findByBedName(name);
	}
	
	public void save(RoomDetails roomDetails)
	{
		roomDetailsRepository.save(roomDetails);
	}
	
	public List<RoomDetails> findByFloorNo(String name)
	{
		return roomDetailsRepository.findByFloorNo(name);
	}

}
