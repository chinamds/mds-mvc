/**
 * Copyright (c) 2016-2017 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.cm.model;

import com.mds.common.model.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.common.model.JsonDateSerializer;
import com.mds.core.ApprovalStatus;
import com.mds.sys.model.Organization;
import com.mds.util.DateUtils;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import  org.apache.commons.lang.builder.HashCodeBuilder;
import  org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="cm_dailylist", uniqueConstraints = @UniqueConstraint(columnNames={"content_name", "gallery_id", "date"}))
@Indexed
@XmlRootElement
public class DailyList extends DataEntity implements Serializable {
    private Date date;
    private String contentName;
    private String description;
    private Gallery gallery;
    private Organization organization;	// organization
    //private int GalleryId;
    //private String approval;
    private ApprovalStatus approvalStatus;
    
    private List<DailyListItem> dailyListItems = Lists.newArrayList();
    
    public DailyList() {
    	super();
    	this.contentName = DateUtils.formatDate(DateUtils.Now(), "yyyyMMddHHmmssSSS");
    }
    
    public DailyList(Date date) {
    	this();
    	this.date = date;
    }
 
    @JsonProperty(value = "Date")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", nullable=false, length=19)
    @Field
    public Date getDate(){
        return this.date;
    }
    
    public void setDate(Date date){
        this.date = date;
    }
    
    @JsonProperty(value = "contentName")
    @Column(name="content_name", nullable=false, length=50)
    @Field
    public String getContentName(){
        return this.contentName;
    }
    
    public void setContentName(String contentName){
        this.contentName = contentName;
    }
    
    @JsonProperty(value = "description")
    @Column(name="description", length=200)
    @Field
    public String getDescription(){
        return this.description;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    @JsonProperty(value = "ApprovalStatus")
    @Column(name="approval_status", nullable=false, length=50)
	@Enumerated(EnumType.STRING)
    @Field
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Transient
	@Field
	public String getOrganizationCode() {
		if (organization != null && !organization.isRoot())
			return organization.getCode();
		
		return "";
	}
    
	@ManyToOne
    @JoinColumn(name="gallery_id", nullable=true)
    public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="dailyList")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<DailyListItem> getDailyListItems() {
		return dailyListItems;
	}

	public void setDailyListItems(List<DailyListItem> dailyListItems) {
		this.dailyListItems = dailyListItems;
	}
	
	@JsonIgnore
	@Transient
    public Long getThumbnailContentObjectId(){
		if (!this.dailyListItems.isEmpty())
			if (!this.dailyListItems.get(0).getDailyListZones().isEmpty())
				return this.dailyListItems.get(0).getDailyListZones().get(0).getContentObject().getId();
		
		return 0L;
    }

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DailyList pojo = (DailyList)o;
        return (new EqualsBuilder()
             .append(date, pojo.date)
             .append(contentName, pojo.contentName)
             .append(description, pojo.description)
             .append(approvalStatus, pojo.approvalStatus)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(date)
             .append(contentName)
             .append(description)
             .append(approvalStatus)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiID").append("='").append(getId()).append("', ");
        sb.append("dtDate").append("='").append(getDate()).append("', ");
        sb.append("contentName").append("='").append(getContentName()).append("', ");
        sb.append("description").append("='").append(getDescription()).append("', ");
        sb.append("approval").append("='").append(getApprovalStatus()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}