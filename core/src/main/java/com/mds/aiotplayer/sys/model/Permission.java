/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.sys.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.core.UserAction;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 权限表
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-4 上午9:38
 * <p>Version: 1.0
 */
@Entity
@Table(name = "sys_permission", uniqueConstraints = @UniqueConstraint(columnNames={"organization_id", "name"}))
@Indexed
@XmlRootElement
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission extends DataEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * display name
     */
    private String name;
    /**
     * permission flag
     */
    private Long permission;

    /**
     * permission description
     */
    private String description;
    
    private Organization organization;	// organization

    /**
     * display or not; also indicate whether it can be used
     * Use this flag for unity in system
     */
    private Boolean show = Boolean.FALSE;
    private List<MenuFunctionPermission> menuFunctionPermissions = Lists.newArrayList(); // menuFunctions
    
    public Permission() {
    	super();
    }
    
    public Permission(UserAction action) {
    	this();
    	this.name = action.toString();
    	this.permission = (long) action.getValue();
    }
    
    public Permission(long id, UserAction action) {
    	this(action);
    	this.id = id;
    }

    @Column(nullable = false, length = 100)
    @Field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //@Column(nullable = false, length = 100, unique = true)
    @Column(nullable = false)
    @JsonIgnore
    public Long getPermission() {
        return permission;
    }

    public void setPermission(Long permission) {
        this.permission = permission;
    }
    
    @JsonIgnore
	@Transient
	public boolean contains(UserAction userAction) {
    	if ((this.permission & userAction.getValue()) > 0) {
			return true;
		}
    	
    	return false;
    }
    
    @JsonIgnore
	@Transient
    public List<UserAction> getPermissions() {
    	List<UserAction> actions = Lists.newArrayList();
    	if (this.permission != null) {
	    	for(UserAction action : UserAction.values()) {
	    		if (action == UserAction.All)
	    			continue;
	    		
	    		if ((this.permission & action.getValue()) > 0) {
	    			actions.add(action);
	    		}
	    	}
    	}
    	
    	return actions;
    }
    
    @Transient
    @Field
    public String getPermissionKey() {
    	List<String> actions = Lists.newArrayList();
    	if (this.permission != null) {
	    	for(UserAction action : UserAction.values()) {
	    		if (action == UserAction.All)
	    			continue;
	    		
	    		if ((this.permission & action.getValue()) > 0) {
	    			actions.add(action.toString());
	    		}
	    	}
    	}
    	
    	return StringUtils.collectionToCommaDelimitedString(actions);
    }
        
    @Transient
    public String getPermissionMsgKey() {
    	List<String> actions = Lists.newArrayList();
    	if (this.permission != null) {
	    	for(UserAction action : UserAction.values()) {
	    		if (action == UserAction.All)
	    			continue;
	    		
	    		if ((this.permission & action.getValue()) > 0) {
	    			actions.add(action.getLabel()); //"<fmt:message key=\"" + action.getLabel() + "\" />"
	    		}
	    	}
    	}
    	
    	return StringUtils.collectionToCommaDelimitedString(actions);
    }

    @Column(length = 1024)
    @Field
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "is_show")
    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
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

    @JsonIgnore
    @OneToMany(mappedBy = "permission", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<MenuFunctionPermission> getMenuFunctionPermissions() {
		return menuFunctionPermissions;
	}
    
    public void setMenuFunctionPermissions(List<MenuFunctionPermission> menuFunctionPermissions) {
		this.menuFunctionPermissions = menuFunctionPermissions;
	}
    
    @Override
	public void copyFrom(Object source) {
    	Permission src = (Permission)source;
		this.name = src.getName(); 	// name
		this.permission = src.getPermission(); 	// permission
		this.show = src.getShow(); 	// show or not
		this.description = src.getDescription();
	}
}
