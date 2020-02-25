package com.example.test.testingHMS.bed.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_room_details_d")
public class RoomDetails implements Serializable{
	
	@Id
	@Column(name="bed_id")
	private String bedId;
	
	@Column(name="room_name")
	private String roomName;
	
	@Column(name="room_type")
	private String roomType;
	
	@Column(name="floor_no")
	private String floorNo;
	
	@Column(name="bed_name")
	private String bedName;
	
	
	@Column(name="luxary_type")
	private String luxaryType;
	
	@Column(name="cost_per_day")
	private long costPerDay;
	
	@Column(name="from_date")
	private Date fromDate;
	
	@Column(name="to_date")
	private Date toDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	private transient String status; 
	
	@JsonIgnore
	@OneToMany(mappedBy="roomDetails",cascade=CascadeType.ALL)
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
		
	

}
