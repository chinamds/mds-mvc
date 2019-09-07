/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.common.model;

import javax.persistence.*;

/**
 * <p>学校信息</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午2:14
 * <p>Version: 1.0
 */
@Entity
@Table(name = "user_schoolinfo")
public class SchoolInfo extends BaseEntity<Long> {

    private User user;
    /**
     * 学校名称
     */
    private String name;

    private SchoolType type;


    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "name", length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "type", length = 2)
    @Enumerated(EnumType.ORDINAL)
    public SchoolType getType() {
        return type;
    }

    public void setType(SchoolType type) {
        this.type = type;
    }
}
