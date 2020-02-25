package com.example.test.testingHMS.bed.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.test.testingHMS.bed.model.RoomDetails;

public interface RoomDetailsService {
	
	public List<RoomDetails> findAll();
	
	public List<RoomDetails> getRooms(String floor,String ward);
	
	public RoomDetails findByBedName(String name);
	
	public void save(RoomDetails roomDetails);
	
	public List<RoomDetails> findByFloorNo(String name);
	
}
