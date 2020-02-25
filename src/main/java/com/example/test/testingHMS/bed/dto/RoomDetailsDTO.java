package com.example.test.testingHMS.bed.dto;

import java.sql.Date;
import java.util.List;

import com.example.test.testingHMS.bed.model.RoomBookingDetails;

public class RoomDetailsDTO {
	
	private String bedId;
	private String roomName;
	private String roomType;
	private String floorNo;
	private String bedName;
	private String luxaryType;
	private long costPerDay;
	private Date fromDate;
	private Date toDate;
	private String createdBy;
	private List<RoomBookingDetails> roomBooking;
	
	public String getBedId() {
		return bedId;
	}
	public void setBedId(String bedId) {
		this.bedId = bedId;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	public String getFloorNo() {
		return floorNo;
	}
	public void setFloorNo(String floorNo) {
		this.floorNo = floorNo;
	}
	public String getBedName() {
		return bedName;
	}
	public void setBedName(String bedName) {
		this.bedName = bedName;
	}
	public String getLuxaryType() {
		return luxaryType;
	}
	public void setLuxaryType(String luxaryType) {
		this.luxaryType = luxaryType;
	}
	public long getCostPerDay() {
		return costPerDay;
	}
	public void setCostPerDay(long costPerDay) {
		this.costPerDay = costPerDay;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public List<RoomBookingDetails> getRoomBooking() {
		return roomBooking;
	}
	public void setRoomBooking(List<RoomBookingDetails> roomBooking) {
		this.roomBooking = roomBooking;
	}
	

}
