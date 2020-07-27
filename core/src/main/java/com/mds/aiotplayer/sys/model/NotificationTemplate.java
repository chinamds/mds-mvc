/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.aiotplayer.sys.model;

import java.io.Serializable;
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
import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.common.model.IdEntity;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.common.model.plugin.LogicDeleteable;

/**
 * Notification template
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-8 下午2:15
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_notification_template")
@Indexed
@XmlRootElement
@DynamicInsert
@DynamicUpdate

public class NotificationTemplate extends IdEntity implements LogicDeleteable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8164414156493835930L;

	/**
     * unique template name 一 use with send notification
     */
    private String name;

    /**
     * notification trigger by
     */
    private NotificationSource source;


    /**
     * template title
     */
    private String title;


    /**
     * template content
     */
    private String template;

    /**
     * is deleted
     */
    private Boolean deleted = Boolean.FALSE;

    @Column(name="name", length=100, nullable=false, unique=true)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    public NotificationSource getSource() {
        return source;
    }

    public void setSource(final NotificationSource source) {
        this.source = source;
    }

    @Column(name="title", length=1024)
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Column(name="template", length=1024)
    public String getTemplate() {
        return template;
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    @Transient
    public void markDeleted() {
        setDeleted(Boolean.TRUE);
    }

}
