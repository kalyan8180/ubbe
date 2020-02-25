/*package com.example.test.testingHMS.bill.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.test.testingHMS.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "v_charge_d")
public class Charge {
	
		@Id
		@Column(name="charge_id")
		private String chargeId;
		
		@Column(name="name")
		private String name;
		
		@Column(name="department")
		private String department;
		
		@Column(name="inserted_by")
		private String insertedBy;
		
		@Column(name="inserted_date")
		private String insertedDate;
		
		
		@Column(name="amount")
		private long amount;
		
		@JsonIgnore
		@OneToMany(mappedBy="chargeId",cascade=CascadeType.ALL)
		private List<ChargeBill> chargeBill;
		
		@ManyToOne(cascade=CascadeType.ALL)
		@JoinColumn(name="user_id")
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
*/