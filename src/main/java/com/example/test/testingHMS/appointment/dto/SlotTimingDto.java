package com.example.test.testingHMS.appointment.dto;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.example.test.testingHMS.appointment.model.DoctorAppointment;
import com.example.test.testingHMS.user.model.User;


	
	public class SlotTimingDto {
		
		private int slotId;
		
		private String fromTime;
		private String toTime;
		private String slot;
		private String status;
		
		private List<DoctorAppointment> slotAppointment;
		
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