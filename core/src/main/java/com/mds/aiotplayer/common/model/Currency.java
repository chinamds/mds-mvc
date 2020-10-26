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

/**
 * Currency Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "common_currency")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class Currency extends DataEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3735666844730133336L;

	//
	private String currencyCode;

	//
	private String currencySymbol;

	//
	private String currencyName;

	//
	private String hundredthName;

	/**
	 * @return the currencyCode
	 */
	@Column(name="currency_code", length=100, nullable=false, unique=true)
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the currencySymbol
	 */
	@Column(name="currency_symbol", length=20)
	public String getCurrencySymbol() {
		return currencySymbol;
	}

	/**
	 * @param currencySymbol the currencySymbol to set
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	/**
	 * @return the currencyName
	 */
	@Column(name="currency_name", length=256)
	public String getCurrencyName() {
		return currencyName;
	}

	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	/**
	 * @return the hundredthName
	 */
	@Column(name="hundredth_name", length=256)
	public String getHundredthName() {
		return hundredthName;
	}

	/**
	 * @param hundredthName the hundredthName to set
	 */
	public void setHundredthName(String hundredthName) {
		this.hundredthName = hundredthName;
	}
}