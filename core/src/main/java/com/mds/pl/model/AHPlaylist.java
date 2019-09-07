/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.pl.model;

import com.mds.common.model.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.common.model.JsonDateSerializer;
import com.mds.pm.model.Player;
import com.mds.sys.model.Organization;

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
@Table(name="pl_ahplaylist", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "strEvent"}))
@Indexed
@XmlRootElement
public class AHPlaylist extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2343080293313310257L;
	private String strEvent;
    private String strDesc;
    private String strTask;
    private Date dtValidity;
    private Long nMaximumLimit;

    private List<Player> players = Lists.newArrayList(); //players
    private Organization organization;	// organization
    
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
    
    @JsonProperty(value = "strEvent")
    @Column(name="strEvent", nullable=false, length=50)
    @Field
    public String getEvent(){
        return this.strEvent;
    }
    
    public void setEvent(String strEvent){
        this.strEvent = strEvent;
    }
    
    @JsonProperty(value = "strDesc")
    @Column(name="strDesc", length=100)
    @Field
    public String getDesc(){
        return this.strDesc;
    }
    
    public void setDesc(String strDesc){
        this.strDesc = strDesc;
    }
    
    @JsonProperty(value = "strTask")
    @Column(name="strTask", length=50)
    @Field
    public String getTask(){
        return this.strTask;
    }
    
    public void setTask(String strTask){
        this.strTask = strTask;
    }
    
    @JsonProperty(value = "dtValidity")
    @JsonSerialize(using = JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dtValidity", length=19)
    @Field
    public Date getValidity(){
        return this.dtValidity;
    }
    
    public void setValidity(Date dtValidity){
        this.dtValidity = dtValidity;
    }
    
    @JsonProperty(value = "nMaximumLimit")
    @Column(name="nMaximumLimit")
    @Field
    public Long getMaximumLimit(){
        return this.nMaximumLimit;
    }
    
    public void setMaximumLimit(Long nMaximumLimit){
        this.nMaximumLimit = nMaximumLimit;
    }
   

    /**
	 * @return the players
	 */
    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "pl_ahplaylist_player", 
			joinColumns = { @JoinColumn(name = "ahplaylist_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "player_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AHPlaylist pojo = (AHPlaylist)o;
        return (new EqualsBuilder()
             .append(strEvent, pojo.strEvent)
             .append(strDesc, pojo.strDesc)
             .append(strTask, pojo.strTask)
             .append(dtValidity, pojo.dtValidity)
             .append(nMaximumLimit, pojo.nMaximumLimit)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(strEvent)
             .append(strDesc)
             .append(strTask)
             .append(dtValidity)
             .append(nMaximumLimit)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("id").append("='").append(getId()).append("', ");
        sb.append("strEvent").append("='").append(getEvent()).append("', ");
        sb.append("strDesc").append("='").append(getDesc()).append("', ");
        sb.append("strTask").append("='").append(getTask()).append("', ");
        sb.append("dtValidity").append("='").append(getValidity()).append("', ");
        sb.append("nMaximumLimit").append("='").append(getMaximumLimit()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}