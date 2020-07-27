/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.aiotplayer.sys.model;

import com.mds.aiotplayer.common.model.DataEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.pm.model.Player;

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
@Table(name="sys_settingmst")
@Indexed
@XmlRootElement
public class SettingMst extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6527799515515389441L;
	private String strGroupName;
    private String strGroupDesc;
    private byte uiType;
    
    private List<Player> players = Lists.newArrayList(); // players
    private List<Setting> settings = Lists.newArrayList(); // settings
    
    @JsonProperty(value = "strGroupName")
    @Column(name="strGroupName", nullable=false, length=50)
    @Field
    public String getGroupName(){
        return this.strGroupName;
    }
    
    public void setGroupName(String strGroupName){
        this.strGroupName = strGroupName;
    }
    
    @JsonProperty(value = "strGroupDesc")
    @Column(name="strGroupDesc", length=200)
    @Field
    public String getGroupDesc(){
        return this.strGroupDesc;
    }
    
    public void setGroupDesc(String strGroupDesc){
        this.strGroupDesc = strGroupDesc;
    }
    
    @JsonProperty(value = "uiType")
    @Column(name="uiType", nullable=false)
    @Type(type="org.hibernate.type.ByteType")
    @FieldBridge(impl = IntegerBridge.class)
    @Field
    public byte getType(){
        return this.uiType;
    }
    
    public void setType(byte uiType){
        this.uiType = uiType;
    }
    
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = " sys_settingmst_player", 
			joinColumns = { @JoinColumn(name = "settingmst_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "player_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="settingMst")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
    public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SettingMst pojo = (SettingMst)o;
        return (new EqualsBuilder()
             .append(strGroupName, pojo.strGroupName)
             .append(strGroupDesc, pojo.strGroupDesc)
             .append(uiType, pojo.uiType)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strGroupName)
             .append(strGroupDesc)
             .append(uiType)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("uiID").append("='").append(getId()).append("', ");
        sb.append("strGroupName").append("='").append(getGroupName()).append("', ");
        sb.append("strGroupDesc").append("='").append(getGroupDesc()).append("', ");
        sb.append("uiType").append("='").append(getType()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}