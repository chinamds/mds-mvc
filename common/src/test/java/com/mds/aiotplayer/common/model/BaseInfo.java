/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>用户基本信息</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午2:08
 * <p>Version: 1.0
 */
@Entity
@Table(name = "user_baseinfo")
public class BaseInfo extends BaseEntity<Long> {
    private User user;

    /**
     * 真实名称
     */
    private String realname;
    /**
     * 性别
     */
    private Sex sex;

    private Date birthday;

    private int age;

    @OneToOne()
    @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "realname", length = 100)
    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    @Column(name = "sex", length = 2)
    @Enumerated(EnumType.ORDINAL)
    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "birthday")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Column(name = "age")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
