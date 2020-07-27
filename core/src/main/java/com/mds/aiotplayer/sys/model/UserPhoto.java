/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">mds</a> All rights reserved.
 */
package com.mds.aiotplayer.sys.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.bridge.builtin.IntegerBridge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.IdEntity;
import com.mds.aiotplayer.util.Collections3;

/**
 * UserPhoto Entity
 * @author John Lee
 * @version 2013-12-05
 */
@Entity
@Table(name = "sys_user_photo")
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate

public class UserPhoto extends IdEntity implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6904601729106242298L;
	private User user;	// photo for user
	private byte[] photo;	// Head portrait
    	
	/**
     * Default constructor - creates a new instance with no values set.
     */
	public UserPhoto() {
		super();
	}
	
	public UserPhoto(UserPhoto userPhoto) {
		this();
		this.id = userPhoto.getId();
		this.user = userPhoto.getUser();
		this.photo = userPhoto.getPhoto();
	}

	/**
     * Create a new instance and set the username.
     *
     * @param username login name for user.
     */
    public UserPhoto(final User user, final byte[] photo) {
    	this();
        this.user = user;
        this.photo = photo;
    }
    
    @Basic(fetch = FetchType.LAZY )
    @Column(name="photo", length = 1048576)
	@JsonIgnore
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (user != null ? user.hashCode() : 0);
    }
    
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("id", this.id)
                .append("user", this.user);

        return sb.toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		final UserPhoto user = (UserPhoto) source;
		
		this.user = user.getUser();
		this.photo = user.getPhoto();
	}
}