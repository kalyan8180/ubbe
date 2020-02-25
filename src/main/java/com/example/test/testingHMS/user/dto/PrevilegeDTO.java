package com.example.test.testingHMS.user.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.example.test.testingHMS.user.model.RolePrivilegationMapping;


public class PrevilegeDTO 
{
	private Long privilegeId;
	
	private String name;
	
	private String privKey;
	
	private String description;
	
	private Long superUserPrivilege;
	
	private Timestamp createdAt;
	
	private Timestamp updatedAt;
	
	private Long deleted;
	
	private Timestamp deletedAt;

	private List<RolePrivilegationMapping> rolePrivilegationMapping;


	public Long getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(Long privilegeId) {
		this.privilegeId = privilegeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrivKey() {
		return privKey;
	}

	public void setPrivKey(String privKey) {
		this.privKey = privKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSuperUserPrivilege() {
		return superUserPrivilege;
	}

	public void setSuperUserPrivilege(Long superUserPrivilege) {
		this.superUserPrivilege = superUserPrivilege;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getDeleted() {
		return deleted;
	}

	public void setDeleted(Long deleted) {
		this.deleted = deleted;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public List<RolePrivilegationMapping> getRolePrivilegationMapping() {
		return rolePrivilegationMapping;
	}

	public void setRolePrivilegationMapping(List<RolePrivilegationMapping> rolePrivilegationMapping) {
		this.rolePrivilegationMapping = rolePrivilegationMapping;
	}

	
	

}
