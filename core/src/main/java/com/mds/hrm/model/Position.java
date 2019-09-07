/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.hrm.model;

import com.google.common.collect.Lists;
import com.mds.common.model.DataEntity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Position
 * <p>User: John Lee
 * <p>Date: 2017/5/27 20:49
 * <p>Version: 1.0
 */
@Entity
@Table(name = "hrm_position")
@Indexed
@XmlRootElement
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Position extends DataEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Position code
     */
    private String code;
    /**
     * position name
     */
    private String name;
    /**
     * position rank
     */
    private int rank;
    
    private int category;

    /**
     * position description
     */
    private String description;
    
    private List<StaffPosition> staffPositions = Lists.newArrayList();	// staff positions
    
    @Column(length = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 256)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="rank", nullable=false)
    @Field
    public int getRank(){
        return this.rank;
    }
    
    public void setRank(int rank){
        this.rank = rank;
    }
    
    @Column(name="category", nullable=false)
    @Field
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Column(length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="position")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<StaffPosition> getStaffPositions() {
		return staffPositions;
	}

	public void setStaffPositions(List<StaffPosition> staffPositions) {
		this.staffPositions = staffPositions;
	}
}
