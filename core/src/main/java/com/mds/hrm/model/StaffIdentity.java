package com.mds.hrm.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.common.model.Country;
import com.mds.common.model.DataEntity;
import com.mds.common.model.JsonDateSerializer;

/**
 * StaffIdentity Entity
 * <p>User: John Lee
 * <p>Date: 2017/8/18 11:03
 * <p>Version: 1.0
 */
@Entity
@Table(name = "hrm_staffidentity")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StaffIdentity extends DataEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8347343677233988536L;

	private Staff staff;

	private IdentityType identityType;

	//
	private String identityNumber;
	
	private Country countryofIssue;
    private Date issueDate;

	private Date ExpiresOn;

	/**
	 * @return the staff
	 */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="staff_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public Staff getStaff() {
		return staff;
	}

	/**
	 * @param staff the staff to set
	 */
	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	/**
	 * @return the identityType
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="identitytype_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public IdentityType getIdentityType() {
		return identityType;
	}

	/**
	 * @param identityType the identityType to set
	 */
	public void setIdentityType(IdentityType identityType) {
		this.identityType = identityType;
	}

	/**
	 * @return the identityNumber
	 */
	@Column(name="identification_number", length=128)
	public String getIdentityNumber() {
		return identityNumber;
	}

	/**
	 * @param identityNumber the identityNumber to set
	 */
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	/**
	 * @return the countryofIssue
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="country_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public Country getCountryofIssue() {
		return countryofIssue;
	}

	/**
	 * @param countryofIssue the countryofIssue to set
	 */
	public void setCountryofIssue(Country countryofIssue) {
		this.countryofIssue = countryofIssue;
	}

	/**
	 * @return the issueDate
	 */
	@JsonProperty(value = "IssueDate")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="issue_date", length=19)
    @Field
	public Date getIssueDate() {
		return issueDate;
	}

	/**
	 * @param issueDate the issueDate to set
	 */
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	/**
	 * @return the expiresOn
	 */
	@JsonProperty(value = "ExpiresOn")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="expires_on", length=19)
    @Field
	public Date getExpiresOn() {
		return ExpiresOn;
	}

	/**
	 * @param expiresOn the expiresOn to set
	 */
	public void setExpiresOn(Date expiresOn) {
		ExpiresOn = expiresOn;
	}
}
