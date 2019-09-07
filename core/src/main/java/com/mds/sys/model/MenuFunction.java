/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.mds.sys.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.ObjectUtils;
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
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.mds.common.utils.excel.annotation.ExcelField;
import com.mds.core.ResourceId;
import com.mds.common.Constants;
import com.mds.cm.model.Tag;
import com.mds.common.model.DataEntity;
import com.mds.common.model.TreeEntity;
import com.mds.i18n.model.NeutralResource;
import com.mds.i18n.model.ResourceCategory;

/**
 * MenuFunction or resource Entity
 * @author John Lee
 * @version 2013-05-15
 */
@Entity
@Table(name = "sys_menu_function", uniqueConstraints = @UniqueConstraint(columnNames={"parent_id", "code"})) //"parent_id", 
@Indexed
@XmlRootElement
@DynamicInsert 
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MenuFunction extends TreeEntity<MenuFunction> {

	private static final long serialVersionUID = 1L;
	private String href; 	// menuFunction URL
	private ResourceId resourceId; 	// menuFunction URL
	private MenuTarget target; 	// target（ mainFrame、_blank、_self、_parent、_top）
	private String icon; 	// icon
	private boolean isShow; 	// show or not（1：yes；0：no）
	private boolean isActiviti; 	// sync to workflow（1：yes；0：no）
	private MenuFunctionType type; // type
	private String action; // action
	private Integer sort;		// sort flag
	
	private Module module;
	
	//private List<MenuFunction> children = Lists.newArrayList();// child menuFunctions
	//private List<Role> roles = Lists.newArrayList(); // roles
	private List<MenuFunctionPermission> menuFunctionPermissions = Lists.newArrayList(); // permissions
	
	public MenuFunction(){
		super();
		this.sort = 30;
		this.isShow = true;
		this.type = MenuFunctionType.m;
	}
	
	public MenuFunction(Long id){
		this();
		this.id = id;
	}
	
	public MenuFunction(MenuFunction parent){
		this();
		this.parent = parent;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@ExcelField(title="menuFunction.parent", complex=1, align=1, sort=20)
	@JsonBackReference
	@Override
	public MenuFunction getParent() {
		return parent;
	}

	@Override
	public void setParent(MenuFunction parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the module
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="module_id", nullable=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JsonIgnore
	public Module getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(Module module) {
		this.module = module;
	}
	

	//@ExcelField(title="menuFunction.code", align=1, sort=21)
	//@Column(length = 100, nullable=true)
	@JsonProperty(value = "Code")
	@ExcelField(title="menuFunction.code", align=1, sort=22)
	@Column(length=100, nullable=false)
	@Field
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}
	
	/*@JsonProperty(value = "Name")
	@ExcelField(title="menuFunction.name", align=1, sort=22)
	@Column(length=256, nullable=false)
	@Field*/
	@JsonIgnore
	@Transient
	@Override
	public String getName() {
		return name;
	}

	@JsonIgnore
	@Transient
	@Override
	public void setName(String name) {
		this.name = name;
	}
	    
    @Transient
	public String getTitle() {
    	/*if (this.titleResource != null)
    		return this.titleResource.getResourceKey();*/
    	if (isRoot())
    		return "";
    	
    	String menuTitle = this.code;
    	if (resourceId != null && resourceId != ResourceId.none) {
			menuTitle = this.resourceId.toString().replace('_', '.');
		}else {
			MenuFunction parentNode = this.parent; 
			while (parentNode != null && !parentNode.isRoot()){
				if (StringUtils.isBlank(menuTitle)){
					menuTitle = parentNode.getCode();
				}else{
					menuTitle = "." + menuTitle;
					menuTitle = parentNode.getCode() + menuTitle;
				}
			
				parentNode = parentNode.getParent();
			}
		}
		
    	return  ResourceCategory.menu.toString() + "." + menuTitle + Constants.Suffix_Title;
	}
    
    @Transient
	public String getDescription() {
    	/*if (this.descriptionResource != null)
    		return this.descriptionResource.getResourceKey();
    	
    	return "";*/
    	if (isRoot())
    		return "";
    	
    	String menuTitle = this.code;
    	if (resourceId != null && resourceId != ResourceId.none) {
			menuTitle = this.resourceId.toString().replace('_', '.');
		}else {
			MenuFunction parentNode = this.parent; 
			while (parentNode != null && !parentNode.isRoot()){
				if (StringUtils.isBlank(menuTitle)){
					menuTitle = parentNode.getCode();
				}else{
					menuTitle = "." + menuTitle;
					menuTitle = parentNode.getCode() + menuTitle;
				}
			
				parentNode = parentNode.getParent();
			}
		}
    	
    	return  ResourceCategory.menu.toString() + "." + menuTitle + Constants.Suffix_Desc;
	}

	/*@ExcelField(title="menuFunction.title", align=1, sort=25)
	@Column(length = 256)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}*/

	@ExcelField(title="menuFunction.href", align=1, sort=30)
	@Column(length = 1024)
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
	@Transient
	@JsonIgnore
	public String getTargetWindow() {
		if (this.target == null || this.target == MenuTarget.notspecified) // || this.target == MenuTarget.mainframe
			return "";
		
		return target.toString();
	}

	@ExcelField(title="menuFunction.target", align=1, sort=35)
	@Column(length = 20)
	/*@Enumerated(EnumType.STRING)*/
	@Convert(converter = MenuTargetConverter.class)
	public MenuTarget getTarget() {
		return target;
	}

	public void setTarget(MenuTarget target) {
		this.target = target;
	}
	
	@ExcelField(title="menuFunction.icon", align=2, sort=40)
	@Column(length = 1024)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@ExcelField(title="menuFunction.isShow", align=2, sort=45)
	@Column(name="is_show")
	public boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(boolean isShow) {
		this.isShow = isShow;
	}
	
	@ExcelField(title="menuFunction.isActiviti", align=2, sort=50)
	@Column(name="is_activiti")
	public boolean getIsActiviti() {
		return isActiviti;
	}

	public void setIsActiviti(boolean isActiviti) {
		this.isActiviti = isActiviti;
	}

	/*@ExcelField(title="menuFunction.permission", align=1, sort=55)
	@Column(length = 100)*/
	@Transient
	@JsonIgnore
	public String getPermission() {
		//return permission;
		/*if (isRoot())
    		return "";
    	
    	String permission = this.code;
		MenuFunction parentNode = this.parent; 
		while (parentNode != null && !parentNode.isRoot()){
			if (StringUtils.isBlank(permission)){
				permission = parentNode.getCode();
			}else{
				permission = "." + permission;
				permission = parentNode.getCode() + permission;
			}
		
			parentNode = parentNode.getParent();
		}
    	
    	return  permission;*/
		if (this.resourceId != null && this.resourceId != ResourceId.none) {
			return this.resourceId.toString().replace('_', ':');
		}
		
		return StringUtils.EMPTY;
	}

	/*public void setPermission(String permission) {
		this.permission = permission;
	}*/

	/*@ExcelField(title="menuFunction.description", align=1, sort=60)
	@Column(length = 1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}*/

	@ExcelField(title="menuFunction.action", align=2, sort=65)
	@Column(length = 100)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@ExcelField(title="menuFunction.sort", align=2, sort=70)
	@Field
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	/**
	 * @return the type
	 */
	@ExcelField(title="menuFunction.type", align=1, sort=71)
	@Column(name = "type", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
	public MenuFunctionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MenuFunctionType type) {
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	@ExcelField(title="menuFunction.resourceId", align=1, sort=72)
	@Column(name = "resource_id", nullable = true, length = 100)
    @Enumerated(EnumType.STRING)
	public ResourceId getResourceId() {
		return resourceId;
	}

	/**
	 * @param type the type to set
	 */
	public void setResourceId(ResourceId resourceId) {
		this.resourceId = resourceId;
	}

	@OneToMany(cascade=CascadeType.ALL, mappedBy = "parent", fetch=FetchType.LAZY)
	@OrderBy(value="sort") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonManagedReference
	@Override
	public List<MenuFunction> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<MenuFunction> children) {
		this.children = children;
	}
	
	/*@ManyToMany(mappedBy = "menuFunctionPermissions", fetch=FetchType.LAZY)
	@OrderBy("id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Role> getRoles() {
		return roles;
	}
		
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}*/
	
	@OneToMany(mappedBy = "menuFunction", fetch=FetchType.LAZY)
	@OrderBy(value="id") 
	@Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnore
	public List<MenuFunctionPermission> getMenuFunctionPermissions() {
		return menuFunctionPermissions;
	}
	
	public void setMenuFunctionPermissions(List<MenuFunctionPermission> menuFunctionPermissions) {
		this.menuFunctionPermissions = menuFunctionPermissions;
	}

	
	/**
     * @return
     */
	@JsonIgnore
	@Transient
    public boolean isHasChildren() {
        return !getChildren().isEmpty();
    }
	
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(this.getParentId())
				.append(this.code)
                .toString();
	}
	
	@Override
	public void copyFrom(Object source) {
		MenuFunction src = (MenuFunction)source;
		this.code = src.getCode(); 	// code
		this.name = src.getName(); 	// name
		//this.titleResource = src.getTitleResource(); 	// menuFunction title
		this.href = src.getHref(); 	// menuFunction URL
		this.target = src.getTarget(); 	// target（ mainFrame、_blank、_self、_parent、_top）
		this.icon = src.getIcon(); 	// icon
		this.isShow = src.getIsShow(); 	// show or not
		this.isActiviti = src.getIsActiviti(); 	
		//this.permission = src.getPermission(); 
		//this.descriptionResource = src.getDescriptionResource(); // description
		this.action = src.getAction(); // action
		this.sort = src.getSort();		// sort flag
		this.parent = src.getParent();
	}
	
	@Transient
	@JsonIgnore
	public String getActivitiGroupId() {
		return ObjectUtils.toString(getPermission());
	}

	@Transient
	@JsonIgnore
	public String getActivitiGroupName() {
		return ObjectUtils.toString(getId());
	}
}