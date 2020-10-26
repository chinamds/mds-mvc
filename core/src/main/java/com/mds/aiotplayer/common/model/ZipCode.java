/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * ZipCode Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "common_zipcode")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class ZipCode extends DataEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5898222641588916240L;

	private State state;

	//
	private String code;

	private ZipCodeType zipCodeType;

	//
	private String city;

	//
	private BigDecimal lat;

	//@Column(name="lon")
	private BigDecimal lon;

	//@Column(name="x_axis")
	private BigDecimal xAxis;

	//@Column(name="y_axis")
	private BigDecimal yAxis;

	//
	private BigDecimal zAxis;

	/**
	 * @return the state
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="state_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the code
	 */
	@Column(name="code", length=100, nullable=false, unique=true)
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the zipCodeType
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="zipcode_type_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public ZipCodeType getZipCodeType() {
		return zipCodeType;
	}

	/**
	 * @param zipCodeType the zipCodeType to set
	 */
	public void setZipCodeType(ZipCodeType zipCodeType) {
		this.zipCodeType = zipCodeType;
	}

	/**
	 * @return the city
	 */
	@Column(name="city", length=256)
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the lat
	 */
	@Column(name="lat")
	public BigDecimal getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	@Column(name="lon")
	public BigDecimal getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}

	/**
	 * @return the xAxis
	 */
	@Column(name="x_axis")
	public BigDecimal getXAxis() {
		return xAxis;
	}

	/**
	 * @param xAxis the xAxis to set
	 */
	public void setXAxis(BigDecimal xAxis) {
		this.xAxis = xAxis;
	}

	/**
	 * @return the yAxis
	 */
	@Column(name="y_axis")
	public BigDecimal getYAxis() {
		return yAxis;
	}

	/**
	 * @param yAxis the yAxis to set
	 */
	public void setYAxis(BigDecimal yAxis) {
		this.yAxis = yAxis;
	}

	/**
	 * @return the zAxis
	 */
	@Column(name="z_axis")
	public BigDecimal getZAxis() {
		return zAxis;
	}

	/**
	 * @param zAxis the zAxis to set
	 */
	public void setZAxis(BigDecimal zAxis) {
		this.zAxis = zAxis;
	}
}