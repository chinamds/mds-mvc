/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

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
import com.mds.aiotplayer.hrm.model.StaffIdentity;

/**
 * Country Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "common_country")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class Country extends DataEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1223536129002523900L;

	//
	private String countryCode;

	//
	private String countryName;
	
	private List<StaffIdentity> staffIdentities = Lists.newArrayList();	// Staff Identities
	private List<State> states = Lists.newArrayList();	// States

	/**
	 * @return the countryCode
	 */
	@Column(name="country_code", length=100, unique=true, nullable=false)
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the countryName
	 */
	@Column(name="country_name", length=256)
	public String getCountryName() {
		return countryName;
	}

	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/**
	 * @return the staffIdentities
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="countryofIssue")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<StaffIdentity> getStaffIdentities() {
		return staffIdentities;
	}

	/**
	 * @param staffIdentities the staffIdentities to set
	 */
	public void setStaffIdentities(List<StaffIdentity> staffIdentities) {
		this.staffIdentities = staffIdentities;
	}

	/**
	 * @return the states
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="country")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<State> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(List<State> states) {
		this.states = states;
	}
}