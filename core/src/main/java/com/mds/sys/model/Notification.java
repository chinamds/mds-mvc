/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.sys.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Indexed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.cm.model.GalleryMapping;
import com.mds.common.model.IdEntity;
import com.mds.common.model.JsonDateSerializer;
import com.mds.common.model.DataEntity;

/**
 * notification data
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-8 下午2:15
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_notification")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FullTextFilterDefs( {
    @FullTextFilterDef(name = "notificationUser", impl = NotificationUserFilterFactory.class),
})
public class Notification extends IdEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3124928041360001564L;

	/**
     *  user who recieve the notification
     */
    private  User user;

    /**
     * notification trigger by
     */
    private NotificationSource source;

    private String title;
    /**
     * notification content
     */
    private String content;

    /**
     * notice date
     */
    private Date date;

    /**
     * is read
     */
    private Boolean read = Boolean.FALSE;


    /**
	 * @return the user
	 */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	@Enumerated(EnumType.STRING)
    public NotificationSource getSource() {
        return source;
    }

    public void setSource(final NotificationSource source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Column(name = "date")
    @JsonSerialize(using = JsonDateSerializer.class)
	@DateTimeFormat(iso=ISO.DATE_TIME)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Column(name = "is_read")
    public Boolean getRead() {
        return read;
    }

    public void setRead(final Boolean read) {
        this.read = read;
    }
}
