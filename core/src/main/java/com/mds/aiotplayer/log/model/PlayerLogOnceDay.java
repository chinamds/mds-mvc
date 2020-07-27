/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.log.model;

import com.mds.aiotplayer.common.model.IdEntity;
import com.mds.aiotplayer.common.model.DataEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.aiotplayer.common.model.JsonDateSerializer;

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
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name="Log_playeronceday", uniqueConstraints = @UniqueConstraint(columnNames={"strUniqueName", "dtLogDate"}))
@Indexed
@XmlRootElement
public class PlayerLogOnceDay extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4116195802974774608L;
	private String strUniqueName;
    private String strPlayer;
    private String strMACAddress;
    private String strMACAddress1;
    private String strDeviceID;
    private String strMACID;
    private Date dtLastSyncTime;
    private BigDecimal nUsedSpace;
    private Date dtLogDate;
     
    @JsonProperty(value = "strUniqueName")
    @Column(name="strUniqueName", nullable=false, length=50)
    @Field
    public String getUniqueName(){
        return this.strUniqueName;
    }
    
    public void setUniqueName(String strUniqueName){
        this.strUniqueName = strUniqueName;
    }
    
    @JsonProperty(value = "strPlayer")
    @Column(name="strPlayer", length=50)
    @Field
    public String getPlayer(){
        return this.strPlayer;
    }
    
    public void setPlayer(String strPlayer){
        this.strPlayer = strPlayer;
    }
    
    @JsonProperty(value = "strMACAddress")
    @Column(name="strMACAddress", nullable=false, length=20)
    @Field
    public String getMACAddress(){
        return this.strMACAddress;
    }
    
    public void setMACAddress(String strMACAddress){
        this.strMACAddress = strMACAddress;
    }
    
    @JsonProperty(value = "strMACAddress1")
    @Column(name="strMACAddress1", nullable=false, length=20)
    @Field
    public String getMACAddress1(){
        return this.strMACAddress1;
    }
    
    public void setMACAddress1(String strMACAddress1){
        this.strMACAddress1 = strMACAddress1;
    }
    
    @JsonProperty(value = "strDeviceID")
    @Column(name="strDeviceID", nullable=false, length=50)
    @Field
    public String getDeviceID(){
        return this.strDeviceID;
    }
    
    public void setDeviceID(String strDeviceID){
        this.strDeviceID = strDeviceID;
    }
    
    @JsonProperty(value = "strMACID")
    @Column(name="strMACID", nullable=false, length=20)
    @Field
    public String getMACID(){
        return this.strMACID;
    }
    
    public void setMACID(String strMACID){
        this.strMACID = strMACID;
    }
    
    @JsonProperty(value = "dtLastSyncTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtLastSyncTime", nullable=false, length=19)
    @Field
    public Date getLastSyncTime(){
        return this.dtLastSyncTime;
    }
    
    public void setLastSyncTime(Date dtLastSyncTime){
        this.dtLastSyncTime = dtLastSyncTime;
    }
    
    @JsonProperty(value = "nUsedSpace")
    @Column(name="nUsedSpace", precision=8, scale=2)
    @Field
    public BigDecimal getNUsedSpace(){
        return this.nUsedSpace;
    }
    
    public void setNUsedSpace(BigDecimal nUsedSpace){
        this.nUsedSpace = nUsedSpace;
    }
    
    @JsonProperty(value = "dtLogDate")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtLogDate", nullable=false, length=19)
    @Field
    public Date getLogDate(){
        return this.dtLogDate;
    }
    
    public void setLogDate(Date dtLogDate){
        this.dtLogDate = dtLogDate;
    }

    /**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerLogOnceDay pojo = (PlayerLogOnceDay)o;
        return (new EqualsBuilder()
             .append(strUniqueName, pojo.strUniqueName)
             .append(strPlayer, pojo.strPlayer)
             .append(strMACAddress, pojo.strMACAddress)
             .append(strMACAddress1, pojo.strMACAddress1)
             .append(strDeviceID, pojo.strDeviceID)
             .append(strMACID, pojo.strMACID)
             .append(dtLastSyncTime, pojo.dtLastSyncTime)
             .append(nUsedSpace, pojo.nUsedSpace)
             .append(dtLogDate, pojo.dtLogDate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strUniqueName)
             .append(strPlayer)
             .append(strMACAddress)
             .append(strMACAddress1)
             .append(strDeviceID)
             .append(strMACID)
             .append(dtLastSyncTime)
             .append(nUsedSpace)
             .append(dtLogDate)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiID").append("='").append(getId()).append("', ");
        sb.append("strUniqueName").append("='").append(getUniqueName()).append("', ");
        sb.append("strPlayer").append("='").append(getPlayer()).append("', ");
        sb.append("strMACAddress").append("='").append(getMACAddress()).append("', ");
        sb.append("strMACAddress1").append("='").append(getMACAddress1()).append("', ");
        sb.append("strDeviceID").append("='").append(getDeviceID()).append("', ");
        sb.append("strMACID").append("='").append(getMACID()).append("', ");
        sb.append("dtLastSyncTime").append("='").append(getLastSyncTime()).append("', ");
        sb.append("nUsedSpace").append("='").append(getNUsedSpace()).append("', ");
        sb.append("dtLogDate").append("='").append(getLogDate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}