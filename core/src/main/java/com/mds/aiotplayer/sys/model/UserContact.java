/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">mds</a> All rights reserved.
 */
package com.mds.aiotplayer.sys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.common.model.Address;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.hrm.model.Staff;
import com.mds.aiotplayer.util.Collections3;

/**
 * User Entity
 * @author John Lee
 * @version 2013-12-05
 */
@Entity
@Table(name = "sys_user_contact")
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate

public class UserContact extends DataEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private User user;	// address belong to user 
	private int type; // Contact type
    private String contact;

	@ManyToOne
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAddress)) {
            return false;
        }

        final UserAddress userAddress = (UserAddress) o;

        return (user != null  && !user.getId().equals(userAddress.getUser().getId()));

    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (contact != null ? contact.hashCode() : 0);
    }
    
	@Override
	public String toString() {
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("type", this.type)
                .append("contact", this.contact);

        return sb.toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		final UserAddress userAddress = (UserAddress) source;
		
		this.user = userAddress.getUser();
	}
}