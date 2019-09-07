/**
 * Copyright (c) 2016-2018 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.common.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * <p> Abstract base class for the entity, providing unified Long ID, basic features and related methods
 * if database is oracle, please reference {@link BaseOracleEntity}
 * <p>User: John Lee
 * <p>Date: 10/11/2017 14:13:22
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
public abstract class IdEntity extends AbstractEntity<Long> {

	protected Long id;

	@Override
    @Id
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
}
