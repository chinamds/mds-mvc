/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
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
 * message replied/forwarded
 * <p>User: John Lee
 * <p>Date: 22/08/2017 15:45
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_message_reply_forward")
@Indexed
@XmlRootElement
/*@Proxy(lazy = true, proxyClass = MyMessageRF.class)*/
public class MyMessageReFw extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7094490422934216600L;

	/**
     * message
     */
	private MyMessage myMessage;
	
	/**
     * original message that been replied/forwarded
     */
	private MyMessage original;
	
	private MessageAction messageAction;
	
    
    /**
     * Default constructor - creates a new instance with no values set.
     */
	public MyMessageReFw() {
		super();
		this.messageAction = MessageAction.re;
	}
	
	public MyMessageReFw(MyMessage myMessage, MyMessage original) {
		this.myMessage = myMessage;
		this.original = original;
		this.messageAction = MessageAction.re;
	}

    /**
	 * @return the Original message
	 */
    @ManyToOne(optional=true, fetch = FetchType.LAZY)
   	@JoinColumn(name="original_id", nullable=true)
   	@NotFound(action = NotFoundAction.IGNORE)
	public MyMessage getOriginal() {
		return original;
	}

	/**
	 * @param original the original message to set
	 */
	public void setOriginal(MyMessage original) {
		this.original = original;
	}

	@ManyToOne(optional=true, fetch = FetchType.LAZY)
    @JoinColumn(name="mymessage_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }

    @Column(name = "message_action", nullable = false, length = 2)
    //@Field(index=Index.YES)
	@Enumerated(EnumType.STRING)
    public MessageAction getMessageAction() {
        return messageAction;
    }

    public void setMessageAction(MessageAction messageAction) {
        this.messageAction = messageAction;
    }    
}
