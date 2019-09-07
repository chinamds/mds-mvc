/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.msg.model;

import com.mds.common.model.DataEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.common.model.JsonDateSerializer;
import com.mds.pm.model.Player;

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
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
@Table(name="msg_ahmessage", uniqueConstraints = @UniqueConstraint(columnNames={"strAHName"}))
@Indexed
@XmlRootElement
public class AHMessage extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8617728213327764541L;
	private String strAHName;
    private String strAHDesc;
    private String strAHDesc1;
    private String strContent;
    private short nStatus;
    private short nLevel;
    private Short nEndType;
    private Short nDelay;
    private short nAutoUpdate;
    private short uiOutput;
    private short nLayout;
    private Short nMessageZone;
    private short nOverlay;
    private boolean bStartImm;
    private boolean bEndManual;
    private Date dtStartTime;
    private Date dtEndTime;
    private Date dtStopTime;
    private Date dtCreateTime;
    
	private List<Player> players = Lists.newArrayList(); // players
	private List<MessageZone> messageZones = Lists.newArrayList(); // message zones
	
     
    @JsonProperty(value = "strAHName")
    @Column(name="strAHName", nullable=false, length=20)
    @Field
    public String getAHName(){
        return this.strAHName;
    }
    
    public void setAHName(String strAHName){
        this.strAHName = strAHName;
    }
    
    @JsonProperty(value = "strAHDesc")
    @Column(name="strAHDesc", length=100)
    @Field
    public String getAHDesc(){
        return this.strAHDesc;
    }
    
    public void setAHDesc(String strAHDesc){
        this.strAHDesc = strAHDesc;
    }
    
    @JsonProperty(value = "strAHDesc1")
    @Column(name="strAHDesc1", length=100)
    @Field
    public String getAHDesc1(){
        return this.strAHDesc1;
    }
    
    public void setAHDesc1(String strAHDesc1){
        this.strAHDesc1 = strAHDesc1;
    }
    
    @JsonProperty(value = "strContent")
    @Column(name="strContent")
    @Type(type="text")
    @Field
    public String getContent(){
        return this.strContent;
    }
    
    public void setContent(String strContent){
        this.strContent = strContent;
    }
    
    @JsonProperty(value = "nStatus")
    @Column(name="nStatus", nullable=false)
    @Field
    public short getStatus(){
        return this.nStatus;
    }
    
    public void setStatus(short nStatus){
        this.nStatus = nStatus;
    }
    
    @JsonProperty(value = "nLevel")
    @Column(name="nLevel", nullable=false)
    @Field
    public short getLevel(){
        return this.nLevel;
    }
    
    public void setLevel(short nLevel){
        this.nLevel = nLevel;
    }
    
    @JsonProperty(value = "nEndType")
    @Column(name="nEndType")
    @Field
    public Short getEndType(){
        return this.nEndType;
    }
    
    public void setEndType(Short nEndType){
        this.nEndType = nEndType;
    }
    
    @JsonProperty(value = "nDelay")
    @Column(name="nDelay")
    @Field
    public Short getDelay(){
        return this.nDelay;
    }
    
    public void setDelay(Short nDelay){
        this.nDelay = nDelay;
    }
    
    @JsonProperty(value = "nAutoUpdate")
    @Column(name="nAutoUpdate", nullable=false)
    @Field
    public short getAutoUpdate(){
        return this.nAutoUpdate;
    }
    
    public void setAutoUpdate(short nAutoUpdate){
        this.nAutoUpdate = nAutoUpdate;
    }
    
    @JsonProperty(value = "uiOutput")
    @Column(name="uiOutput", nullable=false)
    @Field
    public short getOutput(){
        return this.uiOutput;
    }
    
    public void setOutput(short uiOutput){
        this.uiOutput = uiOutput;
    }
    
    @JsonProperty(value = "nLayout")
    @Column(name="nLayout", nullable=false)
    @Field
    public short getLayout(){
        return this.nLayout;
    }
    
    public void setLayout(short nLayout){
        this.nLayout = nLayout;
    }
    
    @JsonProperty(value = "nMessageZone")
    @Column(name="nMessageZone")
    @Field
    public Short getMessageZone(){
        return this.nMessageZone;
    }
    
    public void setMessageZone(Short nMessageZone){
        this.nMessageZone = nMessageZone;
    }
    
    @JsonProperty(value = "nOverlay")
    @Column(name="nOverlay", nullable=false)
    @Field
    public short getOverlay(){
        return this.nOverlay;
    }
    
    public void setOverlay(short nOverlay){
        this.nOverlay = nOverlay;
    }
    
    @JsonProperty(value = "bStartImm")
    @Column(name="bStartImm", nullable=false)
    @Field
    public boolean isStartImm(){
        return this.bStartImm;
    }
    
    public void setStartImm(boolean bStartImm){
        this.bStartImm = bStartImm;
    }
    
    @JsonProperty(value = "bEndManual")
    @Column(name="bEndManual", nullable=false)
    @Field
    public boolean isEndManual(){
        return this.bEndManual;
    }
    
    public void setEndManual(boolean bEndManual){
        this.bEndManual = bEndManual;
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
    
    @JsonProperty(value = "dtStopTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtStopTime", length=19)
    @Field
    public Date getStopTime(){
        return this.dtStopTime;
    }
    
    public void setStopTime(Date dtStopTime){
        this.dtStopTime = dtStopTime;
    }
    
    @JsonProperty(value = "dtCreateTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtCreateTime", nullable=false, length=19)
    @Field
    public Date getCreateTime(){
        return this.dtCreateTime;
    }
    
    public void setCreateTime(Date dtCreateTime){
        this.dtCreateTime = dtCreateTime;
    }
    
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "msg_ahmessage_player", 
			joinColumns = { @JoinColumn(name = "ahmessage_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "player_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

    /**
	 * @return the messageZones
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="AHMessage")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<MessageZone> getMessageZones() {
		return messageZones;
	}

	/**
	 * @param messageZones the messageZones to set
	 */
	public void setMessageZones(List<MessageZone> messageZones) {
		this.messageZones = messageZones;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AHMessage pojo = (AHMessage)o;
        return (new EqualsBuilder()
             .append(strAHName, pojo.strAHName)
             .append(strAHDesc, pojo.strAHDesc)
             .append(strAHDesc1, pojo.strAHDesc1)
             .append(strContent, pojo.strContent)
             .append(nStatus, pojo.nStatus)
             .append(nLevel, pojo.nLevel)
             .append(nEndType, pojo.nEndType)
             .append(nDelay, pojo.nDelay)
             .append(nAutoUpdate, pojo.nAutoUpdate)
             .append(uiOutput, pojo.uiOutput)
             .append(nLayout, pojo.nLayout)
             .append(nMessageZone, pojo.nMessageZone)
             .append(nOverlay, pojo.nOverlay)
             .append(bStartImm, pojo.bStartImm)
             .append(bEndManual, pojo.bEndManual)
             .append(dtStartTime, pojo.dtStartTime)
             .append(dtEndTime, pojo.dtEndTime)
             .append(dtStopTime, pojo.dtStopTime)
             .append(dtCreateTime, pojo.dtCreateTime)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strAHName)
             .append(strAHDesc)
             .append(strAHDesc1)
             .append(strContent)
             .append(nStatus)
             .append(nLevel)
             .append(nEndType)
             .append(nDelay)
             .append(nAutoUpdate)
             .append(uiOutput)
             .append(nLayout)
             .append(nMessageZone)
             .append(nOverlay)
             .append(bStartImm)
             .append(bEndManual)
             .append(dtStartTime)
             .append(dtEndTime)
             .append(dtStopTime)
             .append(dtCreateTime)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiID").append("='").append(getId()).append("', ");
        sb.append("strAHName").append("='").append(getAHName()).append("', ");
        sb.append("strAHDesc").append("='").append(getAHDesc()).append("', ");
        sb.append("strAHDesc1").append("='").append(getAHDesc1()).append("', ");
        sb.append("strContent").append("='").append(getContent()).append("', ");
        sb.append("nStatus").append("='").append(getStatus()).append("', ");
        sb.append("nLevel").append("='").append(getLevel()).append("', ");
        sb.append("nEndType").append("='").append(getEndType()).append("', ");
        sb.append("nDelay").append("='").append(getDelay()).append("', ");
        sb.append("nAutoUpdate").append("='").append(getAutoUpdate()).append("', ");
        sb.append("uiOutput").append("='").append(getOutput()).append("', ");
        sb.append("nLayout").append("='").append(getLayout()).append("', ");
        sb.append("nMessageZone").append("='").append(getMessageZone()).append("', ");
        sb.append("nOverlay").append("='").append(getOverlay()).append("', ");
        sb.append("bStartImm").append("='").append(isStartImm()).append("', ");
        sb.append("bEndManual").append("='").append(isEndManual()).append("', ");
        sb.append("dtStartTime").append("='").append(getStartTime()).append("', ");
        sb.append("dtEndTime").append("='").append(getEndTime()).append("', ");
        sb.append("dtStopTime").append("='").append(getStopTime()).append("', ");
        sb.append("dtCreateTime").append("='").append(getCreateTime()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}