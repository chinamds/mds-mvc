/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.common.model.IdEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-11 下午3:23
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_user_status_history")
@Indexed
@XmlRootElement
public class UserStatusHistory extends IdEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8900647435908897347L;

	/**
     * target user
     */
    private User user;

    /**
     * status changed reason
     */
    private String reason;

    /**
     * original state
     */
    private int originalStatus;

    /**
     * present status
     */
    private int presentStatus;

    /**
     * Operation administrator
     */
    private User opUser;

    /**
     * date time
     */
    private Date opDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	//@Enumerated(EnumType.ORDINAL)
    @JsonProperty(value = "OriginalStatus")
    @Column(name="original_status", nullable=true)
    public int getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(int originalStatus) {
        this.originalStatus = originalStatus;
    }


    //@Enumerated(EnumType.ORDINAL)
    @JsonProperty(value = "PresentStatus")
    @Column(name="present_status", nullable=true)
    public int getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(int presentStatus) {
        this.presentStatus = presentStatus;
    }

    @JsonProperty(value = "Reason")
    @Column(name="reason", nullable=true)
    @Type(type="text")
    @Field
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "op_user_id")
    public User getOpUser() {
        return opUser;
    }

    public void setOpUser(User opUser) {
        this.opUser = opUser;
    }

    @Column(name = "op_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }
}
