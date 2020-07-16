package com.mds.common.model;

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

/**
 * State Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "common_state")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class State extends DataEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1923585784857115144L;

	private Country country;

	//
	private String stateCode;

	//
	private String stateName;
	
	private List<ZipCode> zipCodes = Lists.newArrayList();	// zip codes

	/**
	 * @return the country
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="country_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the stateCode
	 */
	@Column(name="state_code", length=100, nullable=false, unique=true)
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the stateName
	 */
	@Column(name="state_name", length=256)
	public String getStateName() {
		return stateName;
	}

	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * @return the zipCodes
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="state")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<ZipCode> getZipCodes() {
		return zipCodes;
	}

	/**
	 * @param zipCodes the zipCodes to set
	 */
	public void setZipCodes(List<ZipCode> zipCodes) {
		this.zipCodes = zipCodes;
	}
}