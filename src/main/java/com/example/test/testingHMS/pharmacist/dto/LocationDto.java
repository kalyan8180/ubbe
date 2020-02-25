package com.example.test.testingHMS.pharmacist.dto;

import java.sql.Timestamp;

public class LocationDto {
	
				
		private String locationId;
		
		private String locationName;
		
		private String adress1;
		
		private String adress2;
		
		private String city ; 
		
		private String state;
		
		private String pincode; 

		private long contactNo1;

		private long contactNo2;

		private String cretedBy;

		private Timestamp createdDate;

		public String getLocationId() {
			return locationId;
		}

		public void setLocationId(String locationId) {
			this.locationId = locationId;
		}

		public String getLocationName() {
			return locationName;
		}

		public void setLocationName(String locationName) {
			this.locationName = locationName;
		}

		public String getAdress1() {
			return adress1;
		}

		public void setAdress1(String adress1) {
			this.adress1 = adress1;
		}

		public String getAdress2() {
			return adress2;
		}

		public void setAdress2(String adress2) {
			this.adress2 = adress2;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		

		public String getPincode() {
			return pincode;
		}

		public void setPincode(String pincode) {
			this.pincode = pincode;
		}

		public long getContactNo1() {
			return contactNo1;
		}

		public void setContactNo1(long contactNo1) {
			this.contactNo1 = contactNo1;
		}

		public long getContactNo2() {
			return contactNo2;
		}

		public void setContactNo2(long contactNo2) {
			this.contactNo2 = contactNo2;
		}

		public String getCretedBy() {
			return cretedBy;
		}

		public void setCretedBy(String cretedBy) {
			this.cretedBy = cretedBy;
		}

		public Timestamp getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Timestamp createdDate) {
			this.createdDate = createdDate;
		} 
		
		

	}

