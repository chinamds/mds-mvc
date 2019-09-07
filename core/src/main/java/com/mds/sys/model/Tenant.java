/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.sys.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import org.hibernate.annotations.GenericGenerator;
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
import com.mds.common.model.AbstractEntity;
import com.mds.common.model.Address;
import com.mds.common.model.IdEntity;
import com.mds.common.model.TreeEntity;
import com.mds.hrm.model.Staff;
import com.mds.i18n.model.Culture;
import com.mds.common.utils.excel.annotation.ExcelField;

/**
 * Tenant Entity
 * @author John Lee
 * @version 2013-05-15
 */
@Entity
@Table(name = "sys_tenant" )
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tenant extends AbstractEntity<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3315076353539622656L;
	private Organization organization;		// Area
	private String id;
	
	public Tenant(){
		super();
	}
		
	public Tenant(final String id){
		this();
		this.id = id;
	}
	
	public Tenant(Organization organization){
		this();
		this.organization = organization;
	}


	@ExcelField(title="tenant.organization", align=2, sort=10, complex=1)
	@OneToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
				
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(this.getId())
                .toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		Tenant src = (Tenant)source;
		this.id = src.getId(); 	// name
		this.organization = src.getOrganization(); 	// organization code
	}

	/**
     * Get the internal ID (database primary key) of this object
     *
     * @return internal ID of object
     */
	@Id
    @GeneratedValue(generator = "assigned")
    @GenericGenerator(name = "assigned", strategy = "assigned")
	@Column(length=50)
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
}