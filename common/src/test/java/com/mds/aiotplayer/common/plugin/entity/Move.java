/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.plugin.entity;

import com.mds.aiotplayer.common.model.BaseEntity;
import com.mds.aiotplayer.common.model.validate.group.Create;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
//import javax.validation.constraints.NotNull;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-4 上午9:38
 * <p>Version: 1.0
 */
@Entity
@Table(name = "showcase_moveable")
public class Move extends BaseEntity<Long> implements Movable {

    //@NotNull(groups = Create.class)
    private String name;

    private Integer weight;

    private Boolean show;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "weight")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Column(name = "is_show")
    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
