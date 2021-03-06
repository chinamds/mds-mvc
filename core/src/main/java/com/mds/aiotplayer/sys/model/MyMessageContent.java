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
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.common.model.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 消息内容
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-22 下午1:55
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_message_content")
@Indexed
@XmlRootElement
@Proxy(lazy = true, proxyClass = MyMessageContent.class)
public class MyMessageContent extends IdEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5636366704207788745L;

	private MyMessage myMessage;

    /**
     * message content
     */
    private String content;
    
    public MyMessageContent() {
    	super();
    }
    
    public MyMessageContent(MyMessage myMessage) {
    	this();
    	
    	this.myMessage = myMessage;
    }


    /*@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mymessage_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)*/
    @ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="mymessage_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
    public MyMessage getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }

    @XmlAttribute
    @JsonProperty(value = "Content")
    @Column(name="content", nullable=true)
    @Type(type="text")
    @Field
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
