/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.log.model;

import com.mds.common.model.IdEntity;
import com.mds.common.model.DataEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mds.common.model.JsonDateSerializer;

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
@Table(name="log_message", uniqueConstraints = @UniqueConstraint(columnNames={"strUniqueName", "strMessage", "dtStartTime"}))
@Indexed
@XmlRootElement
public class MessageLog extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9117544992226397813L;
	private String strMessage;
    private Date dtStartTime;
    private Date dtEndTime;
    private String strUniqueName;
    private String strPlayer;
    private Date dtLogDate;
     
    @JsonProperty(value = "strMessage")
    @Column(name="strMessage", length=50)
    @Field
    public String getMessage(){
        return this.strMessage;
    }
    
    public void setMessage(String strMessage){
        this.strMessage = strMessage;
    }
    
    @JsonProperty(value = "dtStartTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtStartTime", nullable=false, length=19)
    @Field
    public Date getStartTime(){
        return this.dtStartTime;
    }
    
    public void setStartTime(Date dtStartTime){
        this.dtStartTime = dtStartTime;
    }
    
    @JsonProperty(value = "dtEndTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtEndTime", nullable=false, length=19)
    @Field
    public Date getEndTime(){
        return this.dtEndTime;
    }
    
    public void setEndTime(Date dtEndTime){
        this.dtEndTime = dtEndTime;
    }
    
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

        MessageLog pojo = (MessageLog)o;
        return (new EqualsBuilder()
             .append(strMessage, pojo.strMessage)
             .append(dtStartTime, pojo.dtStartTime)
             .append(dtEndTime, pojo.dtEndTime)
             .append(strUniqueName, pojo.strUniqueName)
             .append(strPlayer, pojo.strPlayer)
             .append(dtLogDate, pojo.dtLogDate)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strMessage)
             .append(dtStartTime)
             .append(dtEndTime)
             .append(strUniqueName)
             .append(strPlayer)
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
        sb.append("strMessage").append("='").append(getMessage()).append("', ");
        sb.append("dtStartTime").append("='").append(getStartTime()).append("', ");
        sb.append("dtEndTime").append("='").append(getEndTime()).append("', ");
        sb.append("strUniqueName").append("='").append(getUniqueName()).append("', ");
        sb.append("strPlayer").append("='").append(getPlayer()).append("', ");
        sb.append("dtLogDate").append("='").append(getLogDate()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}