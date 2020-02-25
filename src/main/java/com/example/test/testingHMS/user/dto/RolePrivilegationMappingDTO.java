package com.example.test.testingHMS.user.dto;

import java.sql.Timestamp;

import com.example.test.testingHMS.user.model.Previlege;
import com.example.test.testingHMS.user.model.Role;
import com.example.test.testingHMS.user.model.User;

public class RolePrivilegationMappingDTO {
	private Long rolePrivilegationId;
	
	private String createdBy;
	
	private Timestamp createdAt;
	
	private String lastModifiedBy;
	
	private Timestamp lastModifiedAt;
	
	private long deleted;
	
	private transient String privilegeName;
	
	private transient String roleName;
	
	private User user;
	
	private Role role;
	
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
