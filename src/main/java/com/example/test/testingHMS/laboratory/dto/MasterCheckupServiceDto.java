package com.example.test.testingHMS.laboratory.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.example.test.testingHMS.user.model.User;

public class MasterCheckupServiceDto {
	
	
		private String masterCheckupId;
		
		private String checkupId;
		
		private String serviceName;
		
		private String masterServiceName;
		
		private Timestamp createdDate;
		
		private String createdBy;
		
		private String modifiedBy;
		
		private Timestamp modifiedDate;
		
		private String masterServiceId;
		
		private User masterCheckUpUser;
		
		 private float cost;
			
		
		private transient List<Map<String, String>> addCheckUp;

		public String getMasterCheckupId() {
			return masterCheckupId;
		}

		public void setMasterCheckupId(String masterCheckupId) {
			this.masterCheckupId = masterCheckupId;
		}

		public String getCheckupId() {
			return checkupId;
		}

		public void setCheckupId(String checkupId) {
			this.checkupId = checkupId;
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public String getMasterServiceName() {
			return masterServiceName;
		}

		public void setMasterServiceName(String masterServiceName) {
			this.masterServiceName = masterServiceName;
		}

		public Timestamp getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Timestamp createdDate) {
			this.createdDate = createdDate;
		}

		public String getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}

		public String getModifiedBy() {
			return modifiedBy;
		}

		public void setModifiedBy(String modifiedBy) {
			this.modifiedBy = modifiedBy;
		}

		public Timestamp getModifiedDate() {
			return modifiedDate;
		}

		public void setModifiedDate(Timestamp modifiedDate) {
			this.modifiedDate = modifiedDate;
		}

		public String getMasterServiceId() {
			return masterServiceId;
		}

		public void setMasterServiceId(String masterServiceId) {
			this.masterServiceId = masterServiceId;
		}

		public User getMasterCheckUpUser() {
			return masterCheckUpUser;
		}

		public void setMasterCheckUpUser(User masterCheckUpUser) {
			this.masterCheckUpUser = masterCheckUpUser;
		}

		public List<Map<String, String>> getAddCheckUp() {
			return addCheckUp;
		}

		public void setAddCheckUp(List<Map<String, String>> addCheckUp) {
			this.addCheckUp = addCheckUp;
		}

		public float getCost() {
			return cost;
		}

		public void setCost(float cost) {
			this.cost = cost;
		}
		
		
		
		


}