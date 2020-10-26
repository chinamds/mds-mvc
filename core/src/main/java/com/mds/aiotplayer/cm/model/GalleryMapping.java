/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.JsonDateSerializer;
import com.mds.aiotplayer.hrm.model.Department;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.common.model.IdEntity;
import com.mds.aiotplayer.common.model.DataEntity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import  org.apache.commons.lang.builder.HashCodeBuilder;
import  org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="cm_gallerymapping", uniqueConstraints = @UniqueConstraint(columnNames={"gallery_id", "organization_id", "user_id"}))
@Indexed
@XmlRootElement
public class GalleryMapping extends IdEntity implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5207552392456992747L;
	/**
	 * 
	 */
    private Gallery gallery;
    private Organization organization;
    private Department department;
	private User user;
	
	public GalleryMapping(){
			super();
	}
    
    public GalleryMapping(final Gallery gallery, final Organization organization, final User user){
		super();
		this.gallery = gallery;
		this.organization = organization;
		this.user = user;
	}
	    
	/**
	 * @return the gallery
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gallery_id", nullable=false)
	public Gallery getGallery() {
		return gallery;
	}
	
	@Transient
	public Long getGalleryId() {
		if (this.gallery != null)
			return this.gallery.getId();
		
		return null;
	}
	
	/**
	 * @param gallery the gallery to set
	 */
	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}
	/**
	 * @return the area
	 */
	/**
	 * @return the organization
	 */
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
    @JoinColumn(name="organization_id", nullable=true)
	public Organization getOrganization() {
		return organization;
	}
	
	@Transient
	public String getOrganizationCode() {
		if (organization != null && !organization.isRoot())
			return organization.getCode();
		
		return "";
	}
	
	@Transient
	public String getUserName() {
		if (user != null)
			return user.getUsername();
		
		return "";
	}
	
	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	/**
	 * @return the department
	 */
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
    @JoinColumn(name="department_id", nullable=true)
	public Department getDepartment() {
		return department;
	}
	/**
	 * @param department the department to set
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * @return the user
	 */
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=true)
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}