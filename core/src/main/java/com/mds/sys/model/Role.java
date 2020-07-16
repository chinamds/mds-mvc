/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">MDS</a> All rights reserved.
 */
package com.mds.sys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
//import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.mds.cm.model.Album;
import com.mds.cm.model.Gallery;
import com.mds.common.model.DataEntity;
import com.mds.core.ResourceId;
import com.mds.core.UserAction;
import com.mds.hrm.model.Department;

/**
 * Role Entity
 * @author John Lee
 * @version 2013-12-05
 */
@Entity
@Table(name = "sys_role", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "name"}))
@Indexed
@XmlRootElement
@NamedQueries({
    @NamedQuery(
            name = "findRoleByName",
            query = "select r from Role r where r.name = :name "
    )
})
@DynamicInsert 
@DynamicUpdate

@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Role extends DataEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Organization organization;	// organization
	private String name; 	// role name
	private String description;// role description
	private RoleType type; // role type
	
	private Tenant tenant;

	private Set<User> users = new HashSet<User>(); // users
	private List<MenuFunctionPermission> menuFunctionPermissions = Lists.newArrayList(); // menuFunctions menuFunctionPermissions
	private List<Organization> organizations = Lists.newArrayList(); // organizations
	private List<Department> departments = Lists.newArrayList(); // departments
	private List<Gallery> galleries = Lists.newArrayList(); // galeries
	private List<Album> albums = Lists.newArrayList(); // albums
	
	 /**
     * Default constructor - creates a new instance with no values set.
     */
	public Role() {
		super();
	}

	/**
     * Create a new instance and set the name.
     *
     * @param name name of the role.
     */
	public Role(final String name) {
		this();
		this.name = name;
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
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name="organization_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Transient
	@Field
	public String getOrganizationCode() {
		if (organization != null && !organization.isRoot())
			return organization.getCode();
		
		return "";
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "sys_role_department", 
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "department_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	/**
	 * @return the deparments
	 */
	public List<Department> getDepartments() {
		return departments;
	}

	/**
	 * @param deparments the deparments to set
	 */
	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "sys_role_gallery", 
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "gallery_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	/**
	 * @return the galleries
	 */
	public List<Gallery> getGalleries() {
		return galleries;
	}

	/**
	 * @param galleries the galleries to set
	 */
	public void setGalleries(List<Gallery> galleries) {
		this.galleries = galleries;
	}

	/**
     * @return the name property (getAuthority required by Acegi's GrantedAuthority interface)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Transient
    @JsonIgnore
    public String getAuthority() {
        return getName();
    }

    @Column(nullable = false, length = 100)
    @Field
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(length = 1024)
	@Field
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	@Column(name="type", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
    @Field
    public RoleType getType(){
        return this.type;
    }
    
    public void setType(RoleType type){
        this.type = type;
    }
	
	@ManyToMany(mappedBy = "roles", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	@Transient
	@JsonIgnore
	public List<Long> getUserIds() {
		List<Long> nameIds = Lists.newArrayList();
		for (User user : users) {
			nameIds.add(user.getId());
		}
		return nameIds;
	}

	@Transient
	@JsonIgnore
	public String getUserIdsString() {
		List<Long> nameIdList = Lists.newArrayList();
		for (User user : users) {
			nameIdList.add(user.getId());
		}
		return StringUtils.join(nameIdList, ",");
	}
	
	@Transient
	@JsonIgnore
    public boolean isPermitted(ResourceId resourceId, UserAction userAction) {
    	if (getType()==RoleType.sa)
    		return true;
    	
    	MenuFunctionPermission menuFunctionPermission = menuFunctionPermissions.stream().filter(mp->mp.getMenuFunction().getResourceId() == resourceId).findFirst().orElse(null);
    	if (menuFunctionPermission == null)
    		return false;
    	
    	if (menuFunctionPermission.getPermission() != null) {
    		return menuFunctionPermission.getPermission().contains(userAction);
    	}
    	
    	return false;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "sys_role_menu_function_permission", 
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "menu_function_permission_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<MenuFunctionPermission> getMenuFunctionPermissions() {
		return menuFunctionPermissions;
	}

	public void setMenuFunctionPermissions(List<MenuFunctionPermission> menuFunctionPermissions) {
		this.menuFunctionPermissions = menuFunctionPermissions;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "sys_role_organization", 
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "organization_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	@Transient
	@JsonIgnore
	public List<Long> getOrganizationIds() {
		List<Long> organizationIds = Lists.newArrayList();
		for (Organization organization : organizations) {
			organizationIds.add(organization.getId());
		}
		return organizationIds;
	}

	@Transient
	public void setOrganizationIds(List<Long> organizationIds) {
		organizations = Lists.newArrayList();
		for (Long organizationId : organizationIds) {
			Organization organization = new Organization();
			organization.setId(organizationId);
			organizations.add(organization);
		}
	}

	@Transient
	@JsonIgnore
	public String getOrganizationIdsString() {
		return StringUtils.join(getOrganizationIds(), ",");
	}
	
	@Transient
	public void setOrganizationIds(String organizationIds) {
		organizations = Lists.newArrayList();
		ArrayList<Long> ids = Lists.newArrayList(); 
		if (organizationIds != null){
			//Long[] ids = StringUtils.split(organizationIds, ",");
			for (String organizationId : StringUtils.split(organizationIds, ",")) {
				ids.add(Long.parseLong(organizationId));
			}
			//setOrganizationIdList(Lists.newArrayList(ids));
			setOrganizationIds(ids);
		}
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "sys_role_album", 
			joinColumns = { @JoinColumn(name = "role_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "album_id") })
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	@JsonIgnore
	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}
	
	/**
	 * 获取权限字符串列表
	 */
	/*@Transient
	public List<String> getPermissions() {
		List<String> permissions = Lists.newArrayList();
		for (MenuFunction menuFunction : menuFunctions) {
			if (menuFunction.getPermission()!=null && !"".equals(menuFunction.getPermission())){
				permissions.add(menuFunction.getPermission());
			}
		}
		return permissions;
	}*/
	
	/**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }

        final Role role = (Role) o;

        return !(name != null ? !name.equals(role.name) : role.name != null);

    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append(this.name)
                .toString();
    }
    
    @Override
	public void copyFrom(Object source) {
		Role src = (Role)source;
		this.name = src.getName(); 	// name
		this.organization = src.getOrganization(); 	// organization
		this.type = src.getType(); 	// Role Type
		this.description = src.getDescription();
	}
}
