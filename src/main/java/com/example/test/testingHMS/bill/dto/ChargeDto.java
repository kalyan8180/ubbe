package com.example.test.testingHMS.bill.dto;

import java.util.List;

import com.example.test.testingHMS.bill.model.ChargeBill;
import com.example.test.testingHMS.user.model.User;

public class ChargeDto {
	
		private String chargeId;
		
		private String name;
		
		private String department;
		
		private String insertedBy;
		
		private String insertedDate;
		
		private long amount;
		
		private List<ChargeBill> chargeBill;
		
		private User userId;
		

		public String getChargeId() {
			return chargeId;
		}

		public void setChargeId(String chargeId) {
			this.chargeId = chargeId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getInsertedBy() {
			return insertedBy;
		}

		public void setInsertedBy(String insertedBy) {
			this.insertedBy = insertedBy;
		}

		public String getInsertedDate() {
			return insertedDate;
		}

		public void setInsertedDate(String insertedDate) {
			this.insertedDate = insertedDate;
		}

		public long getAmount() {
			return amount;
		}

		public void setAmount(long amount) {
			this.amount = amount;
		}

		public List<ChargeBill> getChargeBill() {
			return chargeBill;
		}

		public void setChargeBill(List<ChargeBill> chargeBill) {
			this.chargeBill = chargeBill;
		}

		public User getUserId() {
			return userId;
		}

		public void setUserId(User userId) {
			this.userId = userId;
		}


		

}
