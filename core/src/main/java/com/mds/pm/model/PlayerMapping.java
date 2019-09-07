/**
 * Copyright (c) 2016-2017 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
// Created using LayerGen 4.0

package com.mds.pm.model;

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
@Table(name="pm_playermapping", uniqueConstraints = @UniqueConstraint(columnNames={"player_id", "playergroup_id", "dateFrom"}))
@Indexed
@XmlRootElement
public class PlayerMapping extends DataEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7334939387975990325L;
	private Player player;
    private PlayerGroup playerGroup;
    private Date dateFrom;
    private Date dateTo;
       

    /**
	 * @return the player
	 */
    @ManyToOne
    @JoinColumn(name="player_id", nullable=false)
	public Player getPlayer() {
		return player;
	}


	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}


	/**
	 * @return the playerGroup
	 */
	@ManyToOne
    @JoinColumn(name="playergroup_id", nullable=false)
	public PlayerGroup getPlayerGroup() {
		return playerGroup;
	}


	/**
	 * @param playerGroup the playerGroup to set
	 */
	public void setPlayerGroup(PlayerGroup playerGroup) {
		this.playerGroup = playerGroup;
	}


	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}


	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}


	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}


	/**
    * {@inheritDoc}
    */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerMapping pojo = (PlayerMapping)o;
        return (new EqualsBuilder()
             .append(player.getId(), pojo.getPlayer().getId())
             .append(playerGroup.getId(), pojo.getPlayerGroup().getId())
             .append(dateFrom, pojo.dateFrom)
             .append(dateTo, pojo.dateTo)
             ).isEquals();
    }


    /**
    * {@inheritDoc}
    */
     public int hashCode() {
        return   new  HashCodeBuilder( 17 ,  37 )
             .append(player.getId())
             .append(playerGroup.getId())
             .append(dateFrom)
             .append(dateTo)
             .toHashCode();
    }


    /**
    * {@inheritDoc}
    */
     public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append(" [");
        sb.append("id").append("='").append(getId()).append("', ");
        sb.append("player_id").append("='").append(getPlayer().getId()).append("', ");
        sb.append("playergroup_id").append("='").append(getPlayerGroup().getId()).append("', ");
        sb.append("dateFrom").append("='").append(getDateFrom()).append("', ");
        sb.append("dateTo").append("='").append(getDateTo()).append("', ");
        sb.append("]");
        
        return sb.toString();
    }

}