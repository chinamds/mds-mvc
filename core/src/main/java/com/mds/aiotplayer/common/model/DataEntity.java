/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.sys.util.UserUtils;

/**
 * Data Entity class
 * @author John Lee
 * @version 2014-05-16
 */
@SuppressWarnings("serial")
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
public abstract class DataEntity extends AbstractEntity<Long> {
	
	protected Long id;
	protected String currentUser;
	protected String createdBy;
	protected Date dateAdded;
	protected String lastModifiedBy;
	protected Date dateLastModified;
	
	public DataEntity() {
		super();
	}
	
	public DataEntity(String currentUser) {
		this();
		this.currentUser = currentUser;
	}

	@Override
    @Id
    @DocumentId
    @XmlElement(name = "id")
    @JsonProperty(value = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
	
	//@PrePersist
	@Override
	public void prePersist(){
		super.prePersist();
		fillLog(this.currentUser, true);
	}
	
	@Override
	public void beforeUpdate(Object source){
		super.beforeUpdate(source);
		fillLog(this.currentUser, false);
	}
	
	@Column(name="createdby", length=100)
    @Field
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_added")
    //@Field
	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

    @Column(name="last_modifiedby", length=100)
    @Field
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_lastmodified")
    //@Field
	public Date getDateLastModified() {
		return dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}
	
	@Transient
	@XmlTransient
    @JsonIgnore
	public String getCurrentUser() {		
		return currentUser;
	}
	
	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
	
	@Transient
	//@XmlTransient
    @JsonIgnore
	public void fillLog(String currentUser, boolean addMode) {
		if (addMode){
			this.createdBy = currentUser;
			this.dateAdded = Calendar.getInstance().getTime();
		}
		
		this.lastModifiedBy = currentUser;
		this.dateLastModified = Calendar.getInstance().getTime();
	}
	
	@Transient
	//@XmlTransient
    @JsonIgnore
	public static boolean isIllegalId(long id) {		
		return (id == 0 || id == Long.MIN_VALUE);
	}
}
