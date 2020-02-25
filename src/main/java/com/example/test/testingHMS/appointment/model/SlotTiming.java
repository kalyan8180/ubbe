package com.example.test.testingHMS.appointment.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_slottimings_d")
public class SlotTiming {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="slot_id")
	private int slotId;
	
	@Column(name="from_time")
	private String fromTime;
	@Column(name="to_time")
	private String toTime;
	@Column(name="slot")
	private String slot;
	
	@Column(name="status")
	private String status;
	
	@OneToMany(mappedBy="slotTiming",cascade=CascadeType.ALL)
	private List<DoctorAppointment> slotAppointment;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id")
	private User slotUser;

	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public List<DoctorAppointment> getSlotAppointment() {
		return slotAppointment;
	}

	public void setSlotAppointment(List<DoctorAppointment> slotAppointment) {
		this.slotAppointment = slotAppointment;
	}

	public User getSlotUser() {
		return slotUser;
	}

	public void setSlotUser(User slotUser) {
		this.slotUser = slotUser;
	}
	
	
	
}