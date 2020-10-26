/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.DataEntity;
import com.mds.aiotplayer.common.model.TreeEntity;

/**
 * MenuFunction Entity
 * @author John Lee
 * @version 2013-05-15
 */
@Entity
@Table(name = "sys_menu_function_permission")
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate

public class MenuFunctionPermission extends DataEntity {

	private static final long serialVersionUID = 1L;
	private MenuFunction menuFunction; //menuFunction or resource
	private Permission permission; //permission
	
	private List<Role> roles = Lists.newArrayList(); // roles
	
	public MenuFunctionPermission(){
		super();
	}
	
	public MenuFunctionPermission(Long id){
		this();
		this.id = id;
	}
	
	public MenuFunctionPermission(MenuFunction menuFunction, Permission permission){
		this();
		this.menuFunction = menuFunction;
		this.permission = permission;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="menu_function_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public MenuFunction getMenuFunction() {
		return menuFunction;
	}

	public void setMenuFunction(MenuFunction menuFunction) {
		this.menuFunction = menuFunction;
	}
		
	@ManyToMany(mappedBy = "menuFunctionPermissions", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	
	public List<Role> getRoles() {
		return roles;
	}
		
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	@ManyToOne
	@JoinColumn(name="permission_id", nullable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	public Permission getPermission() {
		return permission;
	}
	
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
}