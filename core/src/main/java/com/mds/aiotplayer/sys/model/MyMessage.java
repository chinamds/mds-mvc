/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.common.model.JsonDateSerializer;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.EncodingType;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.bridge.builtin.StringBridge;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "sys_message")
@Indexed
@XmlRootElement
public class MyMessage extends DataEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3810753283090307056L;

	/**
     * Sender
     */
    private User sender;
    
    /**
     * User
     */
    private User user;

    /**
     * Recipients
     */
    private List<MyMessageRecipient> myMessageRecipients = Lists.newArrayList();

    /**
     * send date time
     */
    private Date sendDate;

    /**
     * title
     */
    private String title;

    /**
     * OneToOne如果不生成字节码代理不能代理 所以改成OneToMany
     */
    private Set<MyMessageContent> contents;

    /**
     * message send state
     */
    private MessageFolder messageFolder = MessageFolder.outbox;
    /**
     * date time message sent
     */
    private Date sentDate;

    /**
     * message type, default to user message
     */
    private MessageType type = MessageType.usr;

    /**
     * is read
     */
    private Boolean read = Boolean.FALSE;
    /**
     * is replied
     */
    private Boolean replied = Boolean.FALSE;
    
    /**
     * priority
     */
    private int priority;

    /**
     * original messages that been replied/forwarded by the message
     */
    private List<MyMessageReFw> originals = Lists.newArrayList();

    /**
     * reply/forward messages with the message
     */
    private List<MyMessageReFw> replies = Lists.newArrayList();
    
    public MyMessage() {
    	super();
    }
    
    public MyMessage(User user, User sender, MessageType type, MessageFolder messageFolder) {
    	this();
    	
    	this.user = user;
    	this.sender = sender;
    	this.type = type;
    	this.messageFolder = messageFolder;
    }


    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = JsonDateSerializer.class)
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso=ISO.DATE_TIME)
    @Column(name = "send_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Field(analyze=Analyze.NO)
    @DateBridge(resolution=Resolution.MINUTE, encoding=EncodingType.STRING)
    @SortableField
    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    @Column(name = "title", nullable = true, length = 1024)
    @Field(index=Index.YES)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "myMessage", orphanRemoval = true)*/
    @Transient
    @JsonIgnore
    public MyMessageContent getContent() {
        if(contents != null && contents.size() > 0) {
            return contents.iterator().next();
        }
        return null;
    }

    @Transient
    @JsonIgnore
    public void setContent(MyMessageContent content) {
        if(contents == null) {
            contents = new HashSet<MyMessageContent>();
        }
        contents.add(content);
    }
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="myMessage")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
    public Set<MyMessageContent> getContents() {
    	if(contents == null) {
            contents = new HashSet<MyMessageContent>();
        }
    	
        return this.contents;
    }
    
    public void setContents(Set<MyMessageContent> contents) {
        this.contents = contents;
    }
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="original")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<MyMessageReFw> getOriginals() {
		return originals;
	}

	public void setOriginals(List<MyMessageReFw> originals) {
		this.originals = originals;
	}

	@JsonIgnore
	@Transient
	public Long getOriginalId() {
		if (originals != null && !originals.isEmpty())
			return originals.get(0).getId();
		
		return 0L;
	}
	
	/**
     * Add messages that message reply/forward
     *
     * @param messages messages that message reply/forward
     */
	@Transient
    public void addOriginals(List<MyMessageReFw> messageRFs) {
    	if (originals == null)
    		originals = Lists.newArrayList();
    	
    	originals.addAll(messageRFs);
    }
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="myMessage")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public List<MyMessageReFw> getReplies() {
		return this.replies;
	}

	public void setReplies(List<MyMessageReFw> replies) {
		this.replies = replies;
	}
	
	/**
     * Add messages that message reply/forward
     *
     * @param messages messages that message reply/forward
     */
	@Transient
    public void addReplies(List<MyMessageReFw> replies) {
    	if (this.replies == null)
    		this.replies = Lists.newArrayList();
    	
    	this.replies.addAll(replies);
    }

    @Column(name = "message_folder", nullable = false, length = 10)
    @Field(index=Index.YES)
    @FieldBridge(impl = MessageFolderBridge.class)
    @Enumerated(EnumType.STRING)
    public MessageFolder getMessageFolder() {
        return messageFolder;
    }

    public void setMessageFolder(MessageFolder messageFolder) {
        this.messageFolder = messageFolder;
    }

    /**
	 * @return the sentDate
	 */
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = JsonDateSerializer.class)
	@DateTimeFormat(iso=ISO.DATE_TIME)
    @Column(name = "sent_date")
    @Temporal(TemporalType.TIMESTAMP)
	public Date getSentDate() {
		return sentDate;
	}

	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@Column(name = "type", nullable = false, length = 3)
    @Field(index=Index.YES)
	@Enumerated(EnumType.STRING)
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Column(name = "is_read")
    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    @Column(name = "is_replied")
    public Boolean getReplied() {
        return replied;
    }

    public void setReplied(Boolean replied) {
        this.replied = replied;
    }


	/**
	 * @return the myMessageRecipients
	 */
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="myMessage")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
	public List<MyMessageRecipient> getMyMessageRecipients() {
		return myMessageRecipients;
	}

	/**
	 * @param myMessageRecipients the myMessageRecipients to set
	 */
	public void setMyMessageRecipients(List<MyMessageRecipient> myMessageRecipients) {
		this.myMessageRecipients = myMessageRecipients;
	}
	
	@Transient
	public String getToRecipients() {
		String recients = "";
		for (MyMessageRecipient recipient : myMessageRecipients) {
			if (recipient.getRecipientType() == RecipientType.to) {
				if (StringUtils.isBlank(recients)){
					recients = recipient.getUser().getUsername();
				}
				else{
					recients += "; ";
					recients += recipient.getUser().getUsername();
				}
			}
		}
		return recients;
	}
	
	@Transient
	public String getCcRecipients() {
		String recients = "";
		for (MyMessageRecipient recipient : myMessageRecipients) {
			if (recipient.getRecipientType() != RecipientType.to) {
				if (StringUtils.isBlank(recients)){
					recients = recipient.getUser().getUsername();
				}
				else{
					recients += "; ";
					recients += recipient.getUser().getUsername();
				}
			}
		}
		return recients;
	}
	
	@Transient
	public List<Long> getMessageRecipientIds() {
		List<Long> recients = Lists.newArrayList();
		for (MyMessageRecipient recipient : myMessageRecipients) {
			recients.add(recipient.getUser().getId());
		}
		
		return recients;
	}
	
	@Transient
	public boolean isMyRecievedMessage(Long userId){
		for (MyMessageRecipient recipient : myMessageRecipients) {
			if (recipient.getUser().getId() == userId && !recipient.getMessageFolder().equals(MessageFolder.junk))
				return true;
		}
		
		return false;
	}
	
	@Transient
	public String getSenderName() {
		if (sender != null)
			return sender.getUsername();
		
		return "";
	}

	/**
	 * @return the User send message
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sender_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	@IndexedEmbedded(includeEmbeddedObjectId=true)
	public User getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(User sender) {
		this.sender = sender;
	}
	
	/**
	 * @return the User owner message
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	@IndexedEmbedded(includeEmbeddedObjectId=true)
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

}
