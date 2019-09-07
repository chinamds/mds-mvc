package com.mds.hrm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Lists;
import com.mds.common.model.DataEntity;

/**
 * IdentityType Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "hrm_identitytype")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IdentityType extends DataEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2782740772177478326L;

	//
	private String identityTypeCode;

	//
	private String identityTypeName;

	//
	private boolean canExpire;
	
	private List<StaffIdentity> staffIdentities = Lists.newArrayList();	// Staff Identities

	/**
	 * @return the identityTypeCode
	 */
	@Column(name="identitytype_code", length=100, nullable=false, unique=true)
	public String getIdentityTypeCode() {
		return identityTypeCode;
	}

	/**
	 * @param identityTypeCode the identityTypeCode to set
	 */
	public void setIdentityTypeCode(String identityTypeCode) {
		this.identityTypeCode = identityTypeCode;
	}

	/**
	 * @return the identityTypeName
	 */
	@Column(name="identitytype_name", length=256)
	public String getIdentityTypeName() {
		return identityTypeName;
	}

	/**
	 * @param identityTypeName the identityTypeName to set
	 */
	public void setIdentityTypeName(String identityTypeName) {
		this.identityTypeName = identityTypeName;
	}

	/**
	 * @return the canExpire
	 */
	@Column(name="can_expire")
	public boolean isCanExpire() {
		return canExpire;
	}

	/**
	 * @param canExpire the canExpire to set
	 */
	public void setCanExpire(boolean canExpire) {
		this.canExpire = canExpire;
	}

	/**
	 * @return the staffIdentities
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="identityType")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<StaffIdentity> getStaffIdentities() {
		return staffIdentities;
	}

	/**
	 * @param staffIdentities the staffIdentities to set
	 */
	public void setStaffIdentities(List<StaffIdentity> staffIdentities) {
		this.staffIdentities = staffIdentities;
	}
}