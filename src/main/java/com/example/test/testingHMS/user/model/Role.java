package com.example.test.testingHMS.user.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "v_role_d")
public class Role implements Serializable{
	@Id
	@Column(name = "roll_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long rollId;
	
	@Column(name = "name")
	private String roleName;
	
	@Column(name = "display_name")
	private String displayName;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "created_at")
	private Timestamp createdAt;
	
	@Column(name = "last_modified_at")
	private String lastModifiedBy;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@Column(name = "deleted")
	private int deleted;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "deleted_at")
	private Timestamp deletedAt;
	
	@JsonIgnore
	@OneToMany(mappedBy="userRole",cascade=CascadeType.ALL)
	private List<User> user;

	@JsonIgnore
	@OneToMany(mappedBy = "role",cascade=CascadeType.ALL)
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

	

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
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

	

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

	
	
	
	

}
