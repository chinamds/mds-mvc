/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.sys.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ParamDef;
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
import com.mds.common.model.TenantSupport;
import com.mds.common.model.TreeEntity;
import com.mds.hrm.model.Staff;
import com.mds.i18n.model.Culture;
import com.mds.common.utils.excel.annotation.ExcelField;

/**
 * Organization Entity
 * @author John Lee
 * @version 2013-05-15
 */
@Entity
@Table(name = "sys_organization", uniqueConstraints = @UniqueConstraint(columnNames={"parent_id", "code"}) )
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate

@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Organization extends TreeEntity<Organization> implements TenantSupport, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8219511492498484904L;
	private Area area;		// Area
	private Culture preferredlanguage;
	private String description; 	// description
	private Address address = new Address();
	private String header; 	// Organization header
	private String phone; 	// telephone
	private String fax; 	// fax
	private String email; 	// email
	private String webSite; //web site
	private boolean available; //available
	//private byte[] logo;
	
	private Tenant tenant;
		
	private List<Staff> staffs = Lists.newArrayList();   // staffs
	//private List<Organization> children = Lists.newArrayList();// child organizations
	private List<GalleryMapping> galleryMappings = Lists.newArrayList();   // gallery mappings
	private List<User> users = Lists.newArrayList();   // users
	private List<Role> roles = Lists.newArrayList();   // roles
	private List<Permission> permissions = Lists.newArrayList();   // users
	private Set<OrganizationLogo> organizationLogos;
	
	public Organization(){
		super();
		this.available = true;
	}
	
	public Organization(final String code){
		this();
		this.code = code;
	}
	
	public Organization(final long id){
		this();
		this.id = id;
	}
	
	public Organization(Organization parent){
		this();
		this.parent = parent;
	}
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="tenant_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	@Column(name = "available", nullable = false)
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@ExcelField(title="organization.parent", align=2, sort=10, complex=1)
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonBackReference
	@Override
	public Organization getParent() {
		return parent;
	}

	@Override
	public void setParent(Organization parent) {
		this.parent = parent;
	}

	@ExcelField(title="organization.area", align=2, sort=15)
	@ManyToOne
	@JoinColumn(name="area_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	/**
	 * @return the preferredlanguage
	 */
	@ExcelField(title="organization.preferredlanguage", align=1, sort=15)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="culture_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonBackReference
	public Culture getPreferredlanguage() {
		return preferredlanguage;
	}

	/**
	 * @param preferredlanguage the preferredlanguage to set
	 */
	public void setPreferredlanguage(Culture preferredlanguage) {
		this.preferredlanguage = preferredlanguage;
	}

	//	@OneToMany(mappedBy = "department", fetch=FetchType.LAZY)
//	@OrderBy(value="id") @Fetch(FetchMode.SUBSELECT)
//	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="organization")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<Staff> getStaffs() {
		return staffs;
	}

	public void setStaffs(List<Staff> staffs) {
		this.staffs = staffs;
	}

	/**
	 * @return the galleryMappings
	 */
	@OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="organization")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<GalleryMapping> getGalleryMappings() {
		return galleryMappings;
	}

	/**
	 * @param galleryMappings the galleryMappings to set
	 */
	public void setGalleryMappings(List<GalleryMapping> galleryMappings) {
		this.galleryMappings = galleryMappings;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	/**
	 * @return the users
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="organization")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<User> getUsers() {
		return users;
	}
	
	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	/**
	 * @return the roles
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="organization")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<Role> getRoles() {
		return roles;
	}
	
	/**
	 * @param roles the roles to set
	 */
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * @return the roles
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="organization")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<Permission> getPermissions() {
		return permissions;
	}

	@OneToMany(cascade=CascadeType.ALL, mappedBy = "parent", fetch=FetchType.LAZY)
	@OrderBy(value="code") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonManagedReference
	@Override
	public List<Organization> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<Organization> children) {
		this.children = children;
	}

	/*@Transient
	public static void sortList(List<Organization> list, List<Organization> sourcelist, Long parentId){
		if (parentId == 0)
		{
			for (int i=0; i<sourcelist.size(); i++){
				Organization e = sourcelist.get(i);
				if (e.isTop()){
					list.add(e);
					//enum all children
					for (int j=0; j<sourcelist.size(); j++){
						Organization childe = sourcelist.get(j);
						if (childe.isChild(e)){
							sortList(list, sourcelist, e.getId());
							break;
						}
					}
				}
			}
		}else{
			for (int i=0; i<sourcelist.size(); i++){
				Organization e = sourcelist.get(i);
				if (e.isChild(parentId)){
					list.add(e);
					// get child node if have
					for (int j=0; j<sourcelist.size(); j++){
						Organization childe = sourcelist.get(j);
						if (childe.isChild(e)){
							sortList(list, sourcelist, e.getId());
							break;
						}
					}
				}
			}
		}
	}*/
		
	@Field
	@Column(length=1024, nullable=true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Embedded
    @IndexedEmbedded
    @ExcelField(title="organization.address", align=2, sort=30, fieldType=Address.class)
    public Address getAddress() {
        return address;
    }

	public void setAddress(Address address) {
		this.address = address;
	}

	@Field
	@Column(length=100, nullable=true)
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Field
	@Column(length=100, nullable=true)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Field
	@Column(length=100, nullable=true)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Field
	@Column(length=255, nullable=true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Field
	@Column(length=1024, nullable=true)
	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	@Column(nullable = false, length = 100)
	@ExcelField(title="organization.code", align=2, sort=20)
	@Field
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}
	
	@JsonProperty(value = "Name")
	@ExcelField(title="organization.name", align=2, sort=21)
	@Column(length=256)
	@Field
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
    /*@Basic(fetch = FetchType.LAZY )
    @Column(name="logo", length = 1048576)
    //@Type(type="org.hibernate.type.WrappedMaterializedBlobType")
	@JsonIgnore
	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}*/
	
	@Transient
	@JsonIgnore
	public byte[] getLogo() {
		if (this.organizationLogos != null && !this.organizationLogos.isEmpty())
			return this.organizationLogos.iterator().next().getLogo();
		
		return null;
	}
	
	@Transient
	@JsonIgnore
	public void addLogo(byte[] logo) {
		if (logo != null) {
			if(organizationLogos == null) {
				organizationLogos = new HashSet<OrganizationLogo>();
	        }
			
			organizationLogos.add(new OrganizationLogo(this, logo));
		}		
	}
		
	/**
	 * @return the organizationLogos
	 */
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy="organization")
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public Set<OrganizationLogo> getOrganizationLogos() {
		if(organizationLogos == null) {
			organizationLogos = new HashSet<OrganizationLogo>();
        }
		
		return organizationLogos;
	}

	/**
	 * @param organizationLogos the organizationLogos to set
	 */
	public void setOrganizationLogos(Set<OrganizationLogo> organizationLogos) {
		this.organizationLogos = organizationLogos;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(this.getParentId())
				.append(this.code)
                .append(this.name)
                .toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		Organization src = (Organization)source;
		this.name = src.getName(); 	// name
		this.code = src.getCode(); 	// organization code
		this.header = src.getHeader(); 	// organization header
		this.email = src.getEmail();
		//this.logo = src.getLogo();
		this.webSite = src.getWebSite();
		this.fax = src.getFax();
		this.phone = src.getPhone();
		this.area = src.getArea();
		this.preferredlanguage = src.getPreferredlanguage();
		this.description = src.getDescription();
		this.available = src.isAvailable();
		this.address = src.getAddress();
		//this.sort = src.getSort();		// sort flag
		this.parent = src.getParent();
	}
}