/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.common.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>用户信息</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午2:06
 * <p>Version: 1.0
 */
@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> {
    private String username;

    private String password;

    private Date registerDate;

    /**
     * 基本信息
     */
    private BaseInfo baseInfo;

    /**
     * 学校信息
     */
    private Set<SchoolInfo> schoolInfoSet;


    @Column(name = "username", unique = true, length = 200)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", length = 200)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "registerDate")
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
        if (baseInfo != null)
        	this.baseInfo.setUser(this);
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    public Set<SchoolInfo> getSchoolInfoSet() {
        if (schoolInfoSet == null) {
            schoolInfoSet = new HashSet<SchoolInfo>();
        }
        return schoolInfoSet;
    }

    public void setSchoolInfoSet(Set<SchoolInfo> schoolInfoSet) {
        this.schoolInfoSet = schoolInfoSet;
    }

    public void addSchoolInfo(SchoolInfo schoolInfo) {
        this.getSchoolInfoSet().add(schoolInfo);
        schoolInfo.setUser(this);
    }
}
