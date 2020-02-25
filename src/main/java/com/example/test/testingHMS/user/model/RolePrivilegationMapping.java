package com.example.test.testingHMS.user.model;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="v_roleprivilegation_d")
public class RolePrivilegationMapping {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long rolePrivilegationId;
	
	@Column(name = "createdBy")
	private String createdBy;
	
	@Column(name = "createdAt")
	private Timestamp createdAt;
	
	@Column(name = "lastModifiedBy")
	private String lastModifiedBy;
	
	@Column(name = "lastModifiedAt")
	private Timestamp lastModifiedAt;
	
	@Column(name = "deleted")
	private long deleted;
	
	private transient String privilegeName;
	
	private transient String roleName;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="role_Id")
	private Role role;
	
	@JsonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="privilegeId")
	private Previlege previlege;
	
	
	
	public Long getRolePrivilegationId() {
		return rolePrivilegationId;
	}
	public void setRolePrivilegationId(Long rolePrivilegationId) {
		this.rolePrivilegationId = rolePrivilegationId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public Timestamp getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(Timestamp lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	public long getDeleted() {
		return deleted;
	}
	public void setDeleted(long deleted) {
		this.deleted = deleted;
	}
	public String getPrivilegeName() {
		return privilegeName;
	}
	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}
	
	public Previlege getPrevilege() {
		return previlege;
	}
	public void setPrevilege(Previlege previlege) {
		this.previlege = previlege;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	

}
