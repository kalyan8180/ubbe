package com.example.test.testingHMS.user.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.example.test.testingHMS.user.model.RolePrivilegationMapping;


public class RoleDTO {

	private Long rollId;
	
	private String name;
	
	private String displayName;
	
	private String createdBy;
	
	private String createdAt;
	
	private String lastModifiedBy;
	
	private Timestamp updatedAt;
	
	private int deleted;
	
	private String description;
	
	private Timestamp deletedAt;

	private List<RolePrivilegationMapping> rolePrivilegationMapping;

	public List<RolePrivilegationMapping> getRolePrivilegationMapping() {
		return rolePrivilegationMapping;
	}

	public void setRolePrivilegationMapping(List<RolePrivilegationMapping> rolePrivilegationMapping) {
		this.rolePrivilegationMapping = rolePrivilegationMapping;
	}

	public Long getRollId() {
		return rollId;
	}

	public void setRollId(Long rollId) {
		this.rollId = rollId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



}
