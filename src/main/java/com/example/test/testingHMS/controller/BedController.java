package com.example.test.testingHMS.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;
import com.example.test.testingHMS.bed.model.RoomDetails;
import com.example.test.testingHMS.bed.serviceImpl.RoomBookingDetailsServiceImpl;
import com.example.test.testingHMS.bed.serviceImpl.RoomDetailsServiceImpl;

@CrossOrigin(origins="*",maxAge=36000)
@RestController
@RequestMapping("/v1/bed")
public class BedController {
	
	public static Logger Logger=LoggerFactory.getLogger(BedController.class);
	
	
	@Autowired
	RoomBookingDetailsServiceImpl roomBookingDetailsServiceImpl;
	
	@Autowired
	RoomDetailsServiceImpl roomDetailsServiceImpl;
	
	//get rooms for particular floor
		@RequestMapping(value="/room/{floor}/{ward}",method=RequestMethod.GET)
		public List<RoomDetails> getRoom(@PathVariable String floor,@PathVariable String ward)
		{
			List<RoomDetails> roomDetails=roomDetailsServiceImpl.getRooms(floor, ward);
			for(RoomDetails roomDetailsInfo:roomDetails)
			{
				RoomBookingDetails roomBookingDetailsInfo=roomBookingDetailsServiceImpl.getroomStatus(roomDetailsInfo.getBedId());
				if(roomBookingDetailsInfo!=null)
				{
					
						roomDetailsInfo.setStatus("OCCUPIED");
					
					
				}
				else
				{
					roomDetailsInfo.setStatus("ALLOCATE");
				}
				
			}
			return roomDetails;
		}
		

		
		//get Count for particular floor
		@RequestMapping(value="/floor",method=RequestMethod.GET)
		public List<Map<String, String>> getRoomCount()
		{
			List<String> floorName=new ArrayList<>();
			List<Integer> roomCount=new ArrayList<>();
			List<RoomDetails> roomDetails=roomDetailsServiceImpl.findAll();
			for(RoomDetails roomDetailsInfo:roomDetails)
			{
				String floor=roomDetailsInfo.getFloorNo();
				if(!floorName.contains(floor))
				{
					floorName.add(floor);
				}
			}
			
			List<Map<String,String>> display=new ArrayList<>();
			Map<String,String> displayInfo=null;
			for(String floorNameInfo:floorName)
			{
				displayInfo=new HashMap<>();
				List<RoomDetails> room=roomDetailsServiceImpl.findByFloorNo(floorNameInfo);
				roomCount.add(room.size());
				
				int occupiedRoom=0;
				for(RoomDetails roomInfo:room)
				{
					if(roomBookingDetailsServiceImpl.getroomStatus(roomInfo.getBedId())!=null)
					{
						occupiedRoom+=1;
					}
				}
				
				displayInfo.put("Floor",floorNameInfo);
				displayInfo.put("Total", String.valueOf(room.size()));
				displayInfo.put("Avaiable",String.valueOf(room.size()-occupiedRoom));
				
				display.add(displayInfo);
			}
			return display;
			
		}
		
		
		//getting rooms for particular floor
		@RequestMapping(value="/floor/{floor}",method=RequestMethod.GET)
		public List<RoomDetails> getRoomForFloor(@PathVariable String floor)
		{
			List<RoomDetails> roomDetails=roomDetailsServiceImpl.findByFloorNo(floor);
			for(RoomDetails roomInfo:roomDetails)
			{
				RoomBookingDetails roomBookingDetails =roomBookingDetailsServiceImpl.getroomStatus(roomInfo.getBedId());
				if(roomBookingDetails!=null)
				{
					
						roomInfo.setStatus("OCCUPIED");
					
					
				}
				else
				{
					roomInfo.setStatus("ALLOCATE");
				}
			}
			
			return roomDetails;
		}
		
		//Get floors and ward
		@RequestMapping(value="/floors/ward",method=RequestMethod.GET)
		public Map<String, List> getFloorAndWard()
		{
			List<String> floorName=new ArrayList<>();
			List<String> wardName=new ArrayList<>();
			List<RoomDetails> roomDetails=roomDetailsServiceImpl.findAll();
			for(RoomDetails roomDetailsInfo:roomDetails)
			{
				String floor=roomDetailsInfo.getFloorNo();
				String ward=roomDetailsInfo.getRoomType();
				if(!floorName.contains(floor))
				{
					floorName.add(floor);
				}
				if(!wardName.contains(ward))
				{
					wardName.add(ward);
				}
			}
			
			Map<String,List> disp=new HashMap<>();
			
			disp.put("floors",floorName);
			disp.put("wards", wardName);
			return disp;
		
		}

}
