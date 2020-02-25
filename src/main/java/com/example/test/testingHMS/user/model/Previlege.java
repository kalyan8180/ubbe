package com.example.test.testingHMS.user.model;

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

@Entity
@Table(name = "v_privileges_d")
public class Previlege 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "privilege_Id")
	private Long privilegeId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "privKey")
	private String privKey;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "super_User_Privilege")
	private Long superUserPrivilege;
	
	@Column(name = " createdAt")
	private Timestamp createdAt;
	
	@Column(name = "updatedAt")
	private Timestamp updatedAt;
	
	@Column(name = "deleted")
	private Long deleted;
	
	@Column(name = "deletedAt")
	private Timestamp deletedAt;

	
	@OneToMany(mappedBy = "previlege",cascade=CascadeType.ALL)
	private List<RolePrivilegationMapping> rolePrivilegationMapping;

	public Previlege() {
	}

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

	public Date getCreatedAt() {
		return createdAt;
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

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	
}
