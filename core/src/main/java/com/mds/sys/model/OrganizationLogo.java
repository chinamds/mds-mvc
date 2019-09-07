/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.sys.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
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
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.cm.model.GalleryMapping;
import com.mds.common.model.Address;
import com.mds.common.model.DataEntity;
import com.mds.common.model.IdEntity;
import com.mds.common.model.TreeEntity;
import com.mds.hrm.model.Staff;
import com.mds.i18n.model.Culture;
import com.mds.common.utils.excel.annotation.ExcelField;

/**
 * Organization Logo Entity
 * @author John Lee
 * @version 2013-05-15
 */
@Entity
@Table(name = "sys_organization_logo")
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OrganizationLogo extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -16345032938837330L;
	private Organization organization; // organization
	private byte[] logo;
			
	public OrganizationLogo(){
		super();
	}
	
	public OrganizationLogo(Organization organization, byte[] logo){
		this();
		this.organization = organization;
		this.logo = logo;
	}


	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}	
	
    @Basic(fetch = FetchType.LAZY )
    @Column(name="logo", length = 1048576)
	@JsonIgnore
	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
		
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(this.organization)
                .toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		OrganizationLogo src = (OrganizationLogo)source;
		this.logo = src.getLogo();
		this.organization = src.getOrganization();
	}
}