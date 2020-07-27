/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.sys.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import com.mds.aiotplayer.common.model.IdEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * message recipient
 * <p>User: John Lee
 * <p>Date: 22/08/2017 15:45
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_message_recipient")
@Indexed
@XmlRootElement
@Proxy(lazy = true, proxyClass = MyMessageRecipient.class)
public class MyMessageRecipient extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7156990530982964684L;
	/**
     * message
     */
	private MyMessage myMessage;
	/**
     * Recipient
     */
	private User user;
	
	private RecipientType recipientType;
	
	/**
     * message recieve state
     */
    private MessageFolder messageFolder = MessageFolder.inbox;
    
    /**
     * recieved  time
     */
    private Date recievedTime;
    
    /**
     * is read
     */
    private Boolean read = Boolean.FALSE;
    /**
     * is replied
     */
    private Boolean replied = Boolean.FALSE;
    
    /**
     * Default constructor - creates a new instance with no values set.
     */
	public MyMessageRecipient() {
		super();
		this.recievedTime = new Date();
	}
	
	public MyMessageRecipient(MyMessage myMessage, User user) {
		this.myMessage = myMessage;
		this.user = user;
		this.recievedTime = new Date();
	}

    /**
	 * @return the Recipient
	 */
    @ManyToOne(fetch = FetchType.LAZY)
   	@JoinColumn(name="user_id", nullable=false)
   	@NotFound(action = NotFoundAction.IGNORE)
	public User getUser() {
		return user;
	}

	/**
	 * @param user the recipient to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mymessage_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }

    @Column(name = "recipient_type", nullable = false, length = 5)
	@Enumerated(EnumType.STRING)
    @Field(index=Index.YES)
    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }
    
    /**
	 * @return the messageFolder
	 */
    @Column(name = "message_folder", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @Field(index=Index.YES)
	public MessageFolder getMessageFolder() {
		return messageFolder;
	}

	/**
	 * @param messageFolder the messageFolder to set
	 */
	public void setMessageFolder(MessageFolder messageFolder) {
		this.messageFolder = messageFolder;
	}

	/**
	 * @return the recievedTime
	 */
    @Column(name = "received_date")
    @Temporal(TemporalType.TIMESTAMP)
	public Date getRecievedTime() {
		return recievedTime;
	}

	/**
	 * @param recievedTime the recievedTime to set
	 */
	public void setRecievedTime(Date recievedTime) {
		this.recievedTime = recievedTime;
	}

	/**
	 * @return the read
	 */
	@Column(name = "is_read")
	public Boolean getRead() {
		return read;
	}

	/**
	 * @param read the read to set
	 */
	public void setRead(Boolean read) {
		this.read = read;
	}

	/**
	 * @return the replied
	 */
	@Column(name = "is_replied")
	public Boolean getReplied() {
		return replied;
	}

	/**
	 * @param replied the replied to set
	 */
	public void setReplied(Boolean replied) {
		this.replied = replied;
	}
	
	@Transient
	public Long getRecipientId() {
		if (user != null)
			return user.getId();
		
		return null;
	}
	
	@Transient
	public String getRecipientName() {
		if (user != null)
			return user.getUsername() + "(" + user.getFullName() + ")";
		
		return "";
	}
	
	@Transient
	public String getRecipientCode() {
		if (user != null)
			return user.getUsername();
		
		return "";
	}
}
