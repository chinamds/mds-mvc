/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.sys.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.SecurityActionsOption;
import com.mds.aiotplayer.core.UserAction;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.sys.util.MDSRole;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.sys.util.UserAccountCollection;
import com.mds.aiotplayer.cm.content.UserGalleryProfile;
import com.mds.aiotplayer.cm.content.UserProfile;
import com.mds.aiotplayer.cm.exception.CannotDeleteAlbumException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.rest.EmailTemplate;
import com.mds.aiotplayer.cm.rest.EmailTemplateForm;
import com.mds.aiotplayer.cm.rest.UserRest;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.EmailUtils;
import com.mds.aiotplayer.cm.util.GalleryUtils;
import com.mds.aiotplayer.cm.util.ProfileUtils;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.Collections3;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.common.utils.security.Encodes;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;

import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;

import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.security.Digests;
import com.mds.aiotplayer.security.model.MdsAuthenticationToken;
import com.mds.aiotplayer.sys.exception.InvalidUserException;
import com.mds.aiotplayer.sys.model.Area;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.MenuFunctionType;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Permission;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.Tenant;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.AreaManager;
import com.mds.aiotplayer.sys.service.MenuFunctionManager;
import com.mds.aiotplayer.sys.service.MenuFunctionPermissionManager;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.service.PermissionManager;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.service.TenantManager;
import com.mds.aiotplayer.sys.service.UserManager;

/**
 * Login user utils
 * @author John Lee
 * @version 2013-5-29
 */
public class UserUtils {
    protected static final Logger log = LoggerFactory.getLogger(UserUtils.class);
		
	public static final String CACHE_USER = "user";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_USER_MENU_LIST = "userMenuList";
	public static final String CACHE_MENUPERMISSION_LIST = "menuPermissionList";
	public static final String CACHE_PERMISSION_LIST = "permissionList";
	public static final String CACHE_ORGANIZATION_LIST = "organizationList";
	
	public static Tenant getTenant(String tenantId){
		if (StringUtils.isNotBlank(tenantId)) {
			TenantManager tenantManager = SpringContextHolder.getBean(TenantManager.class);
			
			return tenantManager.get(tenantId);
		}
			
		return null;
	}
	
	public static UserAccount getUser(){	
		return (UserAccount)getDetail();
	}
	
	public static long getUserId(){	
		UserAccount user =  (UserAccount)getDetail();
		if (user != null) {
			return user.getId();
		}
		
		return Long.MIN_VALUE;
	}
	
	public static long getStaffId(){	
		UserAccount user =  (UserAccount)getDetail();
		if (user != null) {
			return user.getStaffId();
		}
		
		return Long.MIN_VALUE;
	}
	
	public static long getOrganizationId(){	
		UserAccount user =  (UserAccount)getDetail();
		if (user != null) {
			return user.getOrganizationId();
		}
		
		return Long.MIN_VALUE;
	}
		
	public static List<User> getUsers(List<Long> userIds){
		UserManager userManager = SpringContextHolder.getBean(UserManager.class);
		
		return userManager.find(userIds.toArray(new Long[0]));
	}
	
	public static User getUser(final String userName){
		UserManager userManager = SpringContextHolder.getBean(UserManager.class);
		
		return userManager.getUserByUsername(userName);
	}
	
	public static boolean isSysUserLogin(){
		if (getUser() == null)
			return false;
		
		MDSRoleCollection roles;
		try {
			roles = RoleUtils.getMDSRolesForUser(getUser().getUsername());
			
			return roles.stream().anyMatch(r->r.getRoleType() == RoleType.sa || r.getRoleType() == RoleType.ad|| r.getRoleType() == RoleType.ur);
		} catch (InvalidMDSRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			
		return false;
	}
	
	public static boolean hasRole(final String roleName) throws InvalidMDSRoleException{
		if (getUser() == null)
			return false;
		
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(getUser().getUsername());
			
		return roles.stream().anyMatch(r->r.getRoleName().equals(roleName));
	}
	
	public static boolean hasRole(final String[] roleNames) throws InvalidMDSRoleException{
		if (getUser() == null)
			return false;
		
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(getUser().getUsername());
			
		return roles.stream().anyMatch(r->ArrayUtils.contains(roleNames, r.getRoleName()));
	}
	
	public static boolean hasRoleType(final RoleType roleType) throws InvalidMDSRoleException{
		if (getUser() == null)
			return false;
		
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(getUser().getUsername());
			
		return roles.stream().anyMatch(r->r.getRoleType().equals(roleType));
	}
	
	public static boolean hasRoleType(final RoleType[] roleTypes) throws InvalidMDSRoleException{
		if (getUser() == null)
			return false;
		
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(getUser().getUsername());
			
		return roles.stream().anyMatch(r->ArrayUtils.contains(roleTypes, r.getRoleType()));
	}
	
	public static List<Gallery> getUserGalleries(final Long userId){
		return null;
	}
	
	public static MenuFunction getMenuFunctionRoot(){
		return getMenuFunctions().stream().filter(a->a.isRoot()).findFirst().get();
	}
	
	public static MenuFunction getMenuFunctionByResourceId(ResourceId resourceId){
		return getMenuFunctions().stream().filter(m->m.getResourceId() == resourceId).findFirst().orElse(null);
	}
	
	public static long getMenuFunctionId(ResourceId resourceId){
		return getMenuFunctions().stream().filter(m->m.getResourceId() == resourceId).map(m->m.getId()).findFirst().orElse(Long.MIN_VALUE);
	}
	
	public static String getHomePage(HttpServletRequest request) {
		String homePage = "/home";
		MenuFunction menuFunction =  getMenuFunctions().stream().filter(m->m.getResourceId() == ResourceId.home).findFirst().orElse(null);
		if (menuFunction != null && StringUtils.isNotBlank(menuFunction.getHref())) {
			//homePage = request.getContextPath() + menuFunction.getHref();
			homePage = menuFunction.getHref();
		}
		
		return homePage;
	}
	
	public static String getHomePage() {
		String homePage = "";
		MenuFunction menuFunction =  getMenuFunctions().stream().filter(m->m.getResourceId() == ResourceId.home).findFirst().orElse(null);
		if (menuFunction != null && StringUtils.isNotBlank(menuFunction.getHref())) {
			homePage = menuFunction.getHref();
		}
		
		return homePage;
	}
	
	public static List<MenuFunction> getMenuFunctions(){
		/*@SuppressWarnings("unchecked")
		List<MenuFunction> menuFunctions = (List<MenuFunction>)CacheUtils.get(CACHE_MENU_LIST);
		if (menuFunctions == null){
			menuFunctions = menuFunctionDao.findAllList();
			CacheUtils.put(CACHE_MENU_LIST, menuFunctions);
		}
		
		return menuFunctions;*/
		List<MenuFunction> menuFunctions = (List<MenuFunction>)CacheUtils.get(CacheItem.sys_menufunctions);
		if (menuFunctions == null){
			MenuFunctionManager menuFunctionManager = (MenuFunctionManager)SpringContextHolder.getBean(MenuFunctionManager.class);
			menuFunctions = menuFunctionManager.getMenuFunctions();
			List<Long> menuFunctionIds = menuFunctions.stream().filter(m->!m.getIsShow()).map(m->m.getId()).collect(Collectors.toList());
			
			menuFunctions = menuFunctions.stream()
					.filter(m->!menuFunctionIds.contains(m.getId()) && !m.getParentIds().stream().anyMatch(p->menuFunctionIds.contains(p)))
					.collect(Collectors.toList());
			CacheUtils.put(CacheItem.sys_menufunctions, menuFunctions);
		}
		
		return menuFunctions;
	}
	
	public static Area getAreaRoot(){
		return getAreaList().stream().filter(a->a.isRoot()).findFirst().get();
	}
	
	public static List<Area> getAreaList(){
		/*@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
//			User user = getUser();
//			if (user.isAdmin()){
				areaList = areaDao.findAllList();
//			}else{
//				areaList = areaDao.findAllChild(user.getArea().getId(), "%,"+user.getArea().getId()+",%");
//			}
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;*/
		AreaManager areaManager = SpringContextHolder.getBean(AreaManager.class);
		
		return areaManager.getAreas();
	}
		
	public static List<Permission> getPermissions(){
		/*@SuppressWarnings("unchecked")
		List<Permission> permissions = (List<Permission>)CacheUtils.get(CACHE_PERMISSION_LIST);
		if (permissions == null){
			permissions = permissionDao.getAll();
			CacheUtils.put(CACHE_PERMISSION_LIST, permissions);
		}
		
		return permissions;*/
		List<Permission> permissions = (List<Permission>)CacheUtils.get(CacheItem.sys_permissions);
		if (permissions != null){
			return permissions;
		}
		
		PermissionManager permissionManager = SpringContextHolder.getBean(PermissionManager.class);
		permissions = permissionManager.getPermissions();
		if (permissions.isEmpty()) {
			long id = 0;
			for(UserAction action : UserAction.values()) {
				id++;
				permissions.add(new Permission(id, action));
			}
			
		}
		CacheUtils.put(CacheItem.sys_permissions, permissions);
		
		return permissions;
	}
	
	public static List<MenuFunctionPermission> getMenuFunctionPermissions(){
		/*@SuppressWarnings("unchecked")
		List<MenuFunctionPermission> menuFunctionPermissions = (List<MenuFunctionPermission>)CacheUtils.get(CACHE_MENUPERMISSION_LIST);
		if (menuFunctionPermissions == null){
			menuFunctionPermissions = menuFunctionPermissionDao.getAll();
			CacheUtils.put(CACHE_MENUPERMISSION_LIST, menuFunctionPermissions);
		}
		
		return menuFunctionPermissions;*/
		MenuFunctionPermissionManager menuFunctionPermissionManager = SpringContextHolder.getBean(MenuFunctionPermissionManager.class);
		
		return menuFunctionPermissionManager.getMenuFunctionPermissions();
	}
	
	public static List<MenuFunctionPermission> getMenuFunctionPermissions(List<Long> roleIds){
		MenuFunctionPermissionManager menuFunctionPermissionManager = SpringContextHolder.getBean(MenuFunctionPermissionManager.class);
		
		return menuFunctionPermissionManager.findByRoleIds(roleIds);
	}
	
	public static List<MenuFunction> getMenuFunctionList(boolean showMenu) throws InvalidMDSRoleException{
		if (!UserUtils.isAuthenticated()) {
			return null;
		}
		
		UserAccount user = getUser();
		return getMenuFunctionList(user, showMenu);
	}
	
	public static List<MenuFunction> getMenuFunctionList(UserAccount user, boolean showMenu) throws InvalidMDSRoleException{
		long permissionViewId = UserUtils.getPermissions().stream().filter(p->p.contains(UserAction.view)).map(p->p.getId()).findFirst().orElse(Long.MIN_VALUE);
		List<MenuFunction> menuFunctionList = null;
		if (user != null && user.getId() != null)
		{
			if (user.isSystem()){
				//menuFunctionList = menuFunctionDao.findAllList();
				menuFunctionList = getMenuFunctions().stream()
						.filter(m->m.getResourceId() != ResourceId.home && m.getType() == MenuFunctionType.m).collect(Collectors.toList());
			}else{
				//menuFunctionList = menuFunctionDao.findByUserId(user.getId());
				List<Long> menuFunctionIds = Lists.newArrayList();
				MDSRoleCollection roles = user.getRoles();
				for (MDSRole role : roles) {
					for(long menuId : role.getMenuPermissions().keySet()) {
						if (!menuFunctionIds.contains(menuId) && (!showMenu || role.getMenuPermissions(menuId).stream().anyMatch(mp->mp.getRight() == permissionViewId))) {
							menuFunctionIds.add(menuId);
						}
					}
				}
				menuFunctionList = getMenuFunctionByIds(menuFunctionIds);
				/*menuFunctionList = (List<MenuFunction>)getCache(CACHE_USER_MENU_LIST);
				if (menuFunctionList == null){
					
					List<MenuFunctionPermission> menuFunctionPermissions = Lists.newArrayList();
					List roleIds = Collections3.extractToList(user.getRoles(), "id");
					if (roleIds != null && roleIds.size() > 0){
						List<MenuFunctionPermission> menuFunctionPermissionForRoles = menuFunctionPermissionManager.findByRoleIds(roleIds);
						for (MenuFunctionPermission menuFunctionPermission : menuFunctionPermissionForRoles) {
							if (!menuFunctionIds.contains(menuFunctionPermission.getMenuFunction().getId())){
								menuFunctionIds.add(menuFunctionPermission.getMenuFunction().getId());
							}
							if (!menuFunctionPermissions.contains(menuFunctionPermission))
								menuFunctionPermissions.add(menuFunctionPermission);
				        }
			        }
					menuFunctionList = getMenuFunctionByIds(menuFunctionIds);
					putCache(CACHE_USER_MENU_LIST, menuFunctionList);
					putCache(CACHE_MENUPERMISSION_LIST, menuFunctionPermissions);
				}*/
			}
		}
		
		return menuFunctionList;
	}
	
	private static List<Long> createSubMenu(HttpServletRequest request, UserAccount cud, MenuRepository repository, List list, List<Long> parentIds)
    {
       List<Long> subIds = Lists.newArrayList(); 
       for (int i=0; i < list.size(); i++)    
	   {
		   MenuFunction menu=(MenuFunction) list.get(i);
		   
	       if (menu.getParent() != null && parentIds.contains(menu.getParent().getId()))
	       {
	    	   String parentName = (String) menu.getParent().getCode();
	           MenuComponent parentMenu = repository.getMenu(parentName);
	           if (parentMenu != null)    
	           {     
	               repository.addMenu(createMenuComponent(request, cud, menu, parentMenu));  
	               subIds.add(menu.getId());
	           }     
	       }
	   }   
       
       return subIds;
    } 
    
    private static MenuComponent createMenuComponent(HttpServletRequest request, UserAccount cud, MenuFunction menu, MenuComponent parentMenu)    
    {   
       MenuComponent mc = new MenuComponent();   
       mc.setName(menu.getCode());    
       mc.setTitle(I18nUtils.getString(menu.getTitle(), request.getLocale()));
       //mc.setTitle(menu.getTitle());
       /*if (!StringUtils.isBlank(menu.getHref())){
	       if (UserUtils.isMobileDevice())
	    	   mc.setLocation(getServletContext().getContextPath() + menu.getHref());
	       else
	    	   mc.setLocation("javascript:top.$.navmenus.NavMenuInTab(this, '" + I18nUtils.getString(menu.getTitle(), request.getLocale())+ "', '"  + getServletContext().getContextPath() + menu.getHref() + "');");
       }*/
       String url = getMenuUrl(request, menu);       
       if (cud.isSysUser() && !UserUtils.isMobileDevice(request)) {
    	   mc.setLocation("javascript:top.$.navmenus.NavMenuInTab(this, '" 
    			   + mc.getTitle() + "', '"  + url + "');");
       }else{
    	   mc.setLocation(url);
       }
       mc.setUrl(url);
       mc.setAction(menu.getAction());   
       mc.setTarget(menu.getTargetWindow());               
       mc.setDescription(I18nUtils.getString(menu.getDescription(), request.getLocale()));
       if (!StringUtils.isBlank(menu.getIcon()))
    	   mc.setImage(menu.getIcon());
       //mc.setRoles(menu.getPermission()); 
       if (parentMenu != null)
    	   mc.setParent(parentMenu);
   
       return mc;
    }
    
    private static String getMenuUrl(HttpServletRequest request, MenuFunction menu) {
		String url = menu.getHref();
        if(StringUtils.startsWithIgnoreCase(url, "http")) {
            return url;
        }
        if (menu.getResourceId() != null && menu.getResourceId() != ResourceId.none) {
     	   url = UserUtils.getResourceUrl(menu.getResourceId());
        }

        String ctx = request.getContextPath();
        if(url.startsWith(ctx) || url.startsWith("/" + ctx  )) {
            return url;
        }

        if(!url.startsWith("/")) {
            url = "/" + url;
        }
        
        return ctx + url;
    }
    
    /**  
     * load user menu   
     * @throws InvalidMDSRoleException 
     */  
    public static MenuRepository createMenuRepository(UserAccount cud, HttpServletRequest request) throws InvalidMDSRoleException    
    {   
  	   MenuRepository repository = new MenuRepository();   
	   if (cud != null) {
		   List list = UserUtils.getMenuFunctionList(cud, true);
		   if (list != null)
		   {
			   List<Long> menuIds = Lists.newArrayList();
			   for (int i=0; i < list.size(); i++)    
			   {
				   MenuFunction menu=(MenuFunction) list.get(i);
				   if (menu.isTop())
				   {
					   repository.addMenu(createMenuComponent(request, cud, menu, null));
					   menuIds.add(menu.getId());
				   }
			   }
			   
			   while(menuIds.size() > 0)
			   {
				   menuIds = createSubMenu(request, cud, repository, list, menuIds);
			   }
		   }
	   }
	   
	   return repository;
    }
		
	public static List<MenuFunction> getMenuFunctionByIds(List<Long> menuFunctionIds){
		List<MenuFunction> menuFunctions = Lists.newArrayList();
		List<MenuFunction> allMenuFunctions = getMenuFunctions();
		for (MenuFunction menuFunction : allMenuFunctions){
			if (menuFunctionIds.contains(menuFunction.getId())){
				menuFunctions.add(menuFunction);
				MenuFunction currMenu = menuFunction;
				while(currMenu.getParent() != null && !currMenu.getParent().isRoot()) {
					long parentId = currMenu.getParent().getId();
					MenuFunction parent = allMenuFunctions.stream().filter(m->m.getId() == parentId).findFirst().orElse(null);
					if (parent != null && !menuFunctions.contains(parent)) {
						menuFunctions.add(parent);
					}
					
					currMenu = parent;
				}
			}
		}
		
		return menuFunctions;
	}
	
	public static List<MenuFunctionPermission> getUserMenuFunctionPermissions() throws InvalidMDSRoleException{
		UserAccount user = getUser();
		
		return getUserMenuFunctionPermissions(user);
	}
	
	public static List<MenuFunctionPermission> getUserMenuFunctionPermissions(UserAccount user) throws InvalidMDSRoleException{
		if (user.isSystem()) {
			return getMenuFunctionPermissions();
		}else {
			List<Long> menuFunctionPermissionIds = Lists.newArrayList();
			MDSRoleCollection roles = user.getRoles(); //RoleUtils.getMDSRolesForUser(user.getUsername());
			for (MDSRole role : roles) {
				for(Map.Entry<Long, List<Pair<Long, Long>>> entrySet : role.getMenuPermissions().entrySet()) {
					for(Pair<Long, Long> menuFunctionPermissionId : entrySet.getValue()) {
						if (!menuFunctionPermissionIds.contains(menuFunctionPermissionId.getLeft())) {
							menuFunctionPermissionIds.add(menuFunctionPermissionId.getLeft());
						}
					}
				}
			}
			
			//return (List<MenuFunctionPermission>)getCache(CACHE_MENUPERMISSION_LIST);
			return getMenuFunctionPermissions().stream().filter(mp->menuFunctionPermissionIds.contains(mp.getId())).collect(Collectors.toList());
		}
	}
	
	public static List<MenuFunctionPermission> getMenuFunctionPermissionList(List menuFunctionIds){
		/*@SuppressWarnings("unchecked")
		List<MenuFunctionPermission> menuFunctionPermissionList = (List<MenuFunctionPermission>)getCache(CACHE_MENUPERMISSION_LIST);
		if (menuFunctionPermissionList == null){
			menuFunctionPermissionList = menuFunctionPermissionManager.findByMenuFunctionIds(menuFunctionIds);
			putCache(CACHE_MENUPERMISSION_LIST, menuFunctionPermissionList);
		}
		return menuFunctionPermissionList;*/
		return null;
	}
				
	/// <overloads>Retrieve a collection of Organizations.</overloads>
	/// <summary>
	/// Retrieve a collection of all Organizations. The organizations may be returned from a cache. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns an <see cref="OrganizationBoCollection" /> object that contains all Organizations.</returns>
	/// <remarks>
	/// The collection of all Organizations are stored in a cache to improve
	/// performance. <note type = "implementnotes">Note to developer: Any code that modifies the organizations in the data store should purge the cache so 
	///              	that they can be freshly retrieved from the data store during the next request. The cache is identified by the
	///              	<see cref="CacheItem.OrganizationBos" /> enum.</note>
	/// </remarks>
	public static OrganizationBoCollection loadOrganizations(){
		OrganizationBoCollection organizations = (OrganizationBoCollection)CacheUtils.get(CacheItem.sys_organizations);

		if (organizations != null){
			return organizations;
		}

		// No organizations in the cache, so get from data store and add to cache.
		organizations = getOrganizationsFromDataStore();

		organizations.sort();

		CacheUtils.put(CacheItem.sys_organizations, organizations);

		return organizations;
	}
	
	public static OrganizationBoCollection getOrganizationsFromDataStore() {
		OrganizationManager organizationManager = SpringContextHolder.getBean(OrganizationManager.class);
		
		OrganizationBoCollection organizations = new OrganizationBoCollection();
		Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("available", SearchOperator.eq, true);
		List<Organization> organizationList = organizationManager.findAll(searchable);
		
		GalleryBoCollection galleries = CMUtils.loadGalleries();
		for(Organization organization : organizationList) {
			OrganizationBo organizationBo = new OrganizationBo();
			organizationBo.setOrganizationId(organization.getId());
			organizationBo.setParentId(organization.getParentId());
			organizationBo.setCode(organization.getCode());
			organizationBo.setName(organization.getName());
			organizationBo.setDescription(organization.getDescription());
			organizations.add(organizationBo);
			organizationBo.setGalleries(galleries.stream().filter(g->g.getOrganizations().contains(organization.getId())).map(g->g.getGalleryId()).collect(Collectors.toList()));
		}
		
		return organizations;
	}
	public static List<Organization> getOrganizationList(){
		/*@SuppressWarnings("unchecked")
		List<Organization> organizationList = (List<Organization>)getCache(CACHE_ORGANIZATION_LIST);
		if (organizationList == null){
			User user = getUser();
			if (user.isSystem()) {
				organizationList = organizationManager.getOrganizations().stream().filter(a->!a.isRoot()).collect(Collectors.toList());
			}else {
				Organization organization = user.getOrganization();
				if (organization == null) {
					
				}else {
					Searchable searchable = Searchable.newSearchable();
					searchable.addSearchFilter("id", SearchOperator.in, organization.getChildIds());
			        searchable.addSort(Direction.ASC, "code");
			        organizationList = organizationManager.findAll(searchable);
				}
			}
			putCache(CACHE_ORGANIZATION_LIST, organizationList);
		}
		return organizationList;*/
		OrganizationManager organizationManager = SpringContextHolder.getBean(OrganizationManager.class);
		
		return organizationManager.getOrganizations();
	}
	
	public static List<Organization> getOrganizations(List<Long> organizationIds){
		OrganizationManager organizationManager = SpringContextHolder.getBean(OrganizationManager.class);
		
		return organizationManager.find(organizationIds.toArray(new Long[0]));
	}
	
	public static long getOwnerOrganizationId(long galleryId) throws InvalidGalleryException{
		GalleryBo gallery = CMUtils.loadGallery(galleryId);
		
		OrganizationBo organization = getOwnerOrganization(gallery);
		if (organization != null)
			return organization.getOrganizationId();
		
		return Long.MIN_VALUE;
	}
	
	public static OrganizationBo getOwnerOrganization(long galleryId) throws InvalidGalleryException{
		GalleryBo gallery = CMUtils.loadGallery(galleryId);
		
		return getOwnerOrganization(gallery);
	}
	
	public static OrganizationBo getOwnerOrganization(GalleryBo gallery) throws InvalidGalleryException{	
		return loadOrganizations().stream().filter(o->gallery.getOrganizations().contains(o.getOrganizationId())).findFirst().orElse(null);
	}
	
	public static long getUserOrganizationId() throws InvalidMDSRoleException{
		UserAccount user = getUser();
		if (user == null || user.isSystem()) {
			return Organization.getRootId();
		}else {
			if (user.getOrganizationId() != Long.MIN_VALUE)
				return user.getOrganizationId();
			
			return getUserOrganizationIds(user.getUsername()).get(0);
		}
	}
	
	public static List<Long> getUserOrganizationIds() throws InvalidMDSRoleException{
		UserAccount user = getUser();
		if (user == null || user.isSystem()) {
			return loadOrganizations().stream().map(o->o.getOrganizationId()).collect(Collectors.toList());
		}else {
			List<Long> userOrganizationIds = getUserOrganizationIds(user.getUsername());
			return loadOrganizations().stream().filter(o->userOrganizationIds.contains(o.getOrganizationId())).map(o->o.getOrganizationId()).collect(Collectors.toList());
		}
	}
	
	public static List<Long> getOrganizationIds(List<String> organizationCodes) throws InvalidMDSRoleException{
		return loadOrganizations().stream().filter(o->organizationCodes.contains(o.getCode())).map(o->o.getOrganizationId()).collect(Collectors.toList());
	}
	
	public static List<OrganizationBo> getUserOrganizations() throws InvalidMDSRoleException{
		UserAccount user = getUser();
		if (user == null || user.isSystem()) {
			return loadOrganizations();
		}else {
			List<Long> userOrganizationIds = getUserOrganizationIds(user.getUsername());
			return loadOrganizations().stream().filter(o->userOrganizationIds.contains(o.getOrganizationId())).collect(Collectors.toList());
		}
	}
	
	public static List<Long> getOrganizationChildren(long organizationId){
		//return getOrganizationList().stream().filter(o->o.getParentIds().contains(organizationId) || o.getId() == organizationId).map(o->o.getId()).collect(Collectors.toList());
		OrganizationBoCollection organizations = loadOrganizations();
		List<Long> ids = Lists.newArrayList();
		ids.add(organizationId);
		ids = getOrganizationChildren(organizations, ids, ids);
		
		return ids;
	}
	
	public static List<Long> getOrganizationChildren(OrganizationBoCollection organizations, List<Long> pIds, List<Long> ids){
		List<Long> parentIds = Lists.newArrayList();
		for(OrganizationBo organization : organizations) {
			if (pIds.contains(organization.getParentId())) {
				ids.add(organization.getOrganizationId());
				parentIds.add(organization.getOrganizationId());
			}
		}
		if (!parentIds.isEmpty()) {
			ids = getOrganizationChildren(organizations, parentIds, ids);
		}
		
		return ids;
	}
	
	public static List<Long> getUserOrganizationIds(String userName) throws InvalidMDSRoleException{
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(userName);
		List<Long> userOrganizationIds = Lists.newArrayList();
		for(MDSRole role : roles) {
			/*if ((role.getRoleType() == RoleType.oa || role.getRoleType() == RoleType.sa || role.getRoleType() == RoleType.ad) 
					&& role.getOrganizationId() != Long.MIN_VALUE) {
				userOrganizationIds.addAll(role.getOrganizationIds());
			}*/
			for(long oId : role.getOrganizationIds()) {
				if (!userOrganizationIds.contains(oId))
					userOrganizationIds.add(oId);
			}
			
			if (!userOrganizationIds.contains(role.getOrganizationId()))
				userOrganizationIds.add(role.getOrganizationId());
		}
		
		return userOrganizationIds;
	}
		
	public static User getUserById(Long id){
		if(id>0) {
			UserManager userManager = SpringContextHolder.getBean(UserManager.class);
			
			return userManager.get(id);
		} else {
			return null;
		}
	}
	
	public static UserAccount getCurrentUser(Authentication auth, UserManager userManager) {
		UserAccount currentUser;
        if (auth.getPrincipal() instanceof LdapUserDetails) {
            LdapUserDetails ldapDetails = (LdapUserDetails) auth.getPrincipal();
            String username = ldapDetails.getUsername();
            currentUser = UserUtils.toUserAccount(userManager.getUserByUsername(username));
        } else if (auth.getPrincipal() instanceof UserDetails) {
            currentUser = (UserAccount) auth.getPrincipal();
        } else if (auth.getDetails() instanceof UserDetails) {
            currentUser = (UserAccount) auth.getDetails();
        } else {
            throw new AccessDeniedException("User not properly authenticated.");
        }
        return currentUser;
    }
	
	public static boolean isMobileDevice(final HttpServletRequest request){
		if (request.getAttribute("isMobileDevice") != null) {
			return Boolean.TRUE.equals(request.getAttribute("isMobileDevice"));
		}
		/*SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null && ctx.getAuthentication() != null){
			MdsAuthenticationToken token = (MdsAuthenticationToken) ctx.getAuthentication();
			
			return token.isMobileDevice();
		}*/
		String  browserDetails = request.getHeader("User-Agent");
		UserAgent userAgent = UserAgent.parseUserAgentString(browserDetails);
		
		if (userAgent != null && userAgent.getOperatingSystem() != null) {
			boolean isMobileDevice = (userAgent.getOperatingSystem().getDeviceType() == DeviceType.MOBILE 
					|| userAgent.getOperatingSystem().getDeviceType() == DeviceType.TABLET
					|| userAgent.getOperatingSystem().getDeviceType() == DeviceType.WEARABLE); 
			request.setAttribute("isMobileDevice", isMobileDevice);
			
			return isMobileDevice;
		}
		
		return false;
	}
	
	public static UserDetails getDetail(){
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null && ctx.getAuthentication() != null && ctx.getAuthentication().getPrincipal() instanceof UserAccount){
			return (UserDetails)ctx.getAuthentication().getPrincipal();
		}

		return null;
	}
	
	
	/**
	 * Retrieve login user object
	 */
	public static Principal getPrincipal(){
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null && ctx.getAuthentication() != null){
			return (Principal)ctx.getAuthentication().getPrincipal();
		}

		return null;
	}
	
	/**
	 * get current login in User object 
	 * @return
	 */
	public static UserAccount getToken(){
		UserAccount token =  null;
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null && ctx.getAuthentication() != null && ctx.getAuthentication().getPrincipal() instanceof UserAccount){		
			token = (UserAccount)ctx.getAuthentication().getPrincipal();
		}
		
		return token ;
	}
	
	/**
	 * Login
	 * @param user
	 * @param rememberMe
	 * @return
	 */
	public static User login(User user, String pwd, Boolean rememberMe, boolean mobileDevice){
		MdsAuthenticationToken token = new MdsAuthenticationToken(user.getUsername(), pwd, true, mobileDevice);
		//token.setRememberMe(rememberMe); 
		token.setDetails(toUserAccount(user)); 
		SecurityContextHolder.getContext().setAuthentication(token);
		
		return getUser(getToken().getUsername());
	}

	/**
	 * is login
	 * @return
	 */
	public static boolean isAuthenticated() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		return (ctx.getAuthentication() != null && ctx.getAuthentication().getPrincipal() instanceof UserAccount
				&& ctx.getAuthentication().isAuthenticated());
	}
	
	public static boolean isAnonymous() {
        AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            return resolver.isAnonymous(auth);
        }
        
        return true;
    }
	
	//#region Public Static Methods

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions. Un-authenticated users
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested
	/// security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery). If multiple actions are
	/// specified, the method is successful if the user has permission for at least one of the actions. If you require that all actions
	/// be satisfied to be successful, call one of the overloads that accept a SecurityActionsOption and
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	/// <overloads>
	/// Determine if the current user has permission to perform the requested action.
	///   </overloads>
	public static boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		return isUserAuthorized(securityActions, RoleUtils.getMDSRolesForUser(), albumId, galleryId, isPrivate, isVirtualAlbum);
	}
	
	public static boolean isUserAuthorized(int securityActions, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		return isUserAuthorized(securityActions, RoleUtils.getMDSRolesForUser(), albumId, galleryId, isPrivate, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform the specified security actions. Un-authenticated users
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested
	/// security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery).</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public static boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isPrivate, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return isUserAuthorized(securityActions, RoleUtils.getMDSRolesForUser(), albumId, galleryId, isPrivate, secActionsOption, isVirtualAlbum);
	}
	
	public static boolean isUserAuthorized(int securityActions, long albumId, long galleryId, boolean isPrivate, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return isUserAuthorized(securityActions, RoleUtils.getMDSRolesForUser(), albumId, galleryId, isPrivate, secActionsOption, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions. Un-authenticated users
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested
	/// security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery). If multiple actions are
	/// specified, the method is successful if the user has permission for at least one of the actions. If you require that all actions
	/// be satisfied to be successful, call one of the overloads that accept a SecurityActionsOption and
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// for anonymous users. The parameter may be null.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public static boolean isUserAuthorized(SecurityActions securityActions, MDSRoleCollection roles, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return isUserAuthorized(securityActions, roles, albumId, galleryId, isPrivate, SecurityActionsOption.RequireOne, isVirtualAlbum);
	}
	
	public static boolean isUserAuthorized(int securityActions, MDSRoleCollection roles, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return isUserAuthorized(securityActions, roles, albumId, galleryId, isPrivate, SecurityActionsOption.RequireOne, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform the specified security actions. When multiple security actions are passed, use
	/// <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied to be successful or only one item
	/// must be satisfied. Un-authenticated users (anonymous users) are always considered NOT authorized (that is, this method returns
	/// false) except when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or
	/// <see cref="SecurityActions.ViewOriginalContentObject" />, since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery). If multiple actions are
	/// specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied to be successful or
	/// only one item must be satisfied.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// for anonymous users. The parameter may be null.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is a virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public static boolean isUserAuthorized(SecurityActions securityActions, MDSRoleCollection roles, long albumId, long galleryId, boolean isPrivate, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return SecurityGuard.isUserAuthorized(securityActions, roles, albumId, galleryId, isAuthenticated(), isPrivate, secActionsOption, isVirtualAlbum);
	}
	
	public static boolean isUserAuthorized(int securityActions, MDSRoleCollection roles, long albumId, long galleryId, boolean isPrivate, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return SecurityGuard.isUserAuthorized(securityActions, roles, albumId, galleryId, isAuthenticated(), isPrivate, secActionsOption, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> is a site administrator. The user is considered a site
	/// administrator if at least one role has Allow Administer Site permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a site administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserSiteAdministrator(MDSRoleCollection roles) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		return SecurityGuard.isUserSiteAdministrator(roles);
	}

    /// <summary>
    /// Determine whether the currently logged-on user is a approval content user. The user is considered a approval
    /// content user if at least one role has Allow approval content object.
    /// </summary>
    /// <returns>
    /// 	<c>true</c> if the user is a approval content user; otherwise, <c>false</c>.
    /// </returns>
    public static boolean isCurrentUserApprovalContent() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
        return SecurityGuard.isUserApprovalContent(RoleUtils.getMDSRolesForUser());
    }

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles"/> is a gallery administrator for the specified
	/// <paramref name="galleryId"/>. The user is considered a gallery administrator if at least one role has Allow Administer Gallery permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a gallery administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserGalleryAdministrator(MDSRoleCollection roles, long galleryId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return SecurityGuard.isUserGalleryAdministrator(roles, galleryId);
	}

	/// <summary>
	/// Determine whether the currently logged-on user is a site administrator. The user is considered a site
	/// administrator if at least one role has Allow Administer Site permission.
	/// </summary>
	/// <returns>
	/// 	<c>true</c> if the user is a site administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isCurrentUserSysAdministrator() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		return isUserSiteAdministrator(RoleUtils.getMDSRolesForUser());
	}

	/// <summary>
	/// Determine whether the currently logged-on user is a gallery administrator for the specified <paramref name="galleryId"/>. 
	/// The user is considered a gallery administrator if at least one role has Allow Administer Gallery permission.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a gallery administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isCurrentUserGalleryAdministrator(long galleryId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException	{
		return SecurityGuard.isUserGalleryAdministrator(RoleUtils.getMDSRolesForUser(), galleryId);
	}
	
	/**
	 * logout
	 */
	public static void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	/**
	 * Retrieve login user name
	 */
	public static String getLoginName(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && (auth.getPrincipal() instanceof UserAccount)) {
        	UserAccount user = (UserAccount) auth.getPrincipal();
            
            return user.getUsername();
        }
        
		return null;
	}
	
	public static boolean isPermitted(String p) throws InvalidMDSRoleException {
		String[] tags = p.split(":");
		if (tags.length == 3) {
			ResourceId resourceId = ResourceId.getResourceId(tags[0] + "_" + tags[1]);
			if (resourceId == ResourceId.none)
				return false;
			
			UserAction userAction = UserAction.getUserAction(tags[2]);
			if (userAction == UserAction.notspecified)
				return false;
			
			return isPermitted(resourceId, userAction);
		}
						 
		 return false;
	 }
	
	 public static boolean isPermitted(ResourceId resourceId, UserAction userAction) throws InvalidMDSRoleException {
		 long menuId = getMenuFunctionList(false).stream().filter(m->m.getResourceId() == resourceId).map(m->m.getId()).findFirst().orElse(Long.MIN_VALUE);
		 if (menuId == Long.MIN_VALUE)
			 return false;
		 
		 /*if (!getMenuFunctionList(false).stream().anyMatch(m->m.getResourceId() == resourceId))
			 return false;
		 
		List<MenuFunctionPermission> menuFunctionPermissions = getMenuFunctionPermissions();//(List<MenuFunctionPermission>)getCache(CACHE_MENUPERMISSION_LIST);
		if (menuFunctionPermissions == null)
			return false;
		
		if (!menuFunctionPermissions.stream().anyMatch(p->p.getMenuFunction().getResourceId() == resourceId && p.getPermission().getPermissions().contains(userAction)))
			return false;*/
		long permissionId = UserUtils.getPermissions().stream().filter(p->p.contains(userAction)).map(p->p.getId()).findFirst().orElse(Long.MIN_VALUE);
		for (MDSRole role : RoleUtils.getMDSRolesForUser())	{
			List<Pair<Long, Long>> menuPermissions = role.getMenuPermissions(menuId);
			if (menuPermissions != null && menuPermissions.stream().anyMatch(mp->mp.getRight() == permissionId)) {
				return true;
			}
		}
				 
		 return false;
	 }
	 
	 public static String getResourceUrl(ResourceId resourceId) {
      String src = resourceId.toString();

      /*if (resourceId == ResourceId.album || resourceId == ResourceId.contentobject)
        src = String.Concat(Utils.GalleryRoot, "/pages/media.jsp");
      else
        src = String.Concat(Utils.GalleryRoot, "/pages/", page, ".jsp");*/

/*      if (src.IndexOf("/sys_", StringComparison.Ordinal) >= 0)
        src = src.Replace("/admin_", "/admin/");
      if (src.IndexOf("/error", StringComparison.Ordinal) >= 0)
        src = src.Replace("/error_", "/error/");
      if (src.IndexOf("/task_", StringComparison.Ordinal) >= 0)
        src = src.Replace("/task_", "/task/");*/
      src = "/" + src.replace('_', '/');

      return src;
    }
	
	public static Authentication getSession(){
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx != null){
			return ctx.getAuthentication();
		}
		
		/*try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){
			
		}*/
		return null;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
		//Object obj = getSession().getAttribute(key);
		//return obj==null?defaultValue:obj;
		return null;
	}

	public static void putCache(String key, Object value) {
		//getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
		//getSession().removeAttribute(key);
	}
   
	//#region Public Static Methods

	/// <summary>
	/// Gets a collection of all the users in the database. The users may be returned from a cache.
	/// </summary>
	/// <returns>Returns a collection of all the users in the database.</returns>
	public static UserAccountCollection getAllUsers(){
		UserAccountCollection usersCache = (UserAccountCollection)CacheUtils.get(CacheItem.sys_users);

		if (usersCache == null)	{
			usersCache = new UserAccountCollection();

			UserManager userManager = SpringContextHolder.getBean(UserManager.class);
			int totalRecords;
			List<User> users = userManager.getAll();
			for (User user : users){
				usersCache.add(toUserAccount(user));
			}

			CacheUtils.put(CacheItem.sys_users, usersCache);
		}

		return usersCache;
	}

	/// <summary>
	/// Populates the properties of <paramref name="userToLoad" /> with information about the user. Requires that the
	/// <see cref="UserAccount.getUserName()" /> property of the <paramref name="userToLoad" /> parameter be assigned a value.
	/// If no user with the specified username exists, no action is taken.
	/// </summary>
	/// <param name="userToLoad">The user account whose properties should be populated.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userToLoad" /> is null.</exception>
	public static void loadUser(UserAccount userToLoad)	{
		if (userToLoad == null)
			throw new ArgumentNullException("userToLoad");

		if (StringUtils.isBlank(userToLoad.getUserName())){
			throw new ArgumentException("The UserName property of the userToLoad parameter must have a valid value. Instead, it was null or empty.");
		}

		UserAccount user = getUserAccount(userToLoad.getUserName(), false);
		if (user != null){
			user.copyTo(userToLoad);
		}
	}

	/// <overloads>
	/// Gets information from the data source for a user.
	/// </overloads>
	/// <summary>
	/// Gets information from the data source for the current logged-on membership user.
	/// </summary>
	/// <returns>A <see cref="UserAccount"/> representing the current logged-on membership user.</returns>
	public static UserAccount getUserAccount()	{
		return StringUtils.isBlank(UserUtils.getLoginName()) ? null : UserUtils.getUser();
	}

	/// <summary>
	/// Gets information from the data source for a user. Provides an option to update the last-activity date/time stamp for the user. 
	/// Returns null if no matching user is found.
	/// </summary>
	/// <param name="userName">The name of the user to get information for.</param>
	/// <param name="userIsOnline"><c>true</c> to update the last-activity date/time stamp for the user; <c>false</c> to return user 
	/// information without updating the last-activity date/time stamp for the user.</param>
	/// <returns>A <see cref="UserAccount"/> object populated with the specified user's information from the data source.</returns>
	public static UserAccount getUserAccount(String userName, boolean userIsOnline){
		UserManager userManager = SpringContextHolder.getBean(UserManager.class);
		
		return toUserAccount(userManager.getUserByUsername(userName));
	}

	/// <summary>
	/// Gets a collection of users the current user has permission to view. Users who have administer site permission can view all users,
	/// as can gallery administrators when the application setting <see cref="IAppSetting.AllowGalleryAdminToViewAllUsersAndRoles"/> is true. When
	/// the setting is false, gallery admins can only view users in galleries they have gallery admin permission in. Note that
	/// a user may be able to view a user but not update it. This can happen when the user belongs to roles that are associated with
	/// galleries the current user is not an admin for. The users may be returned from a cache. Guaranteed to not return null.
	/// This overload is slower than <see cref="GetUsersCurrentUserCanView(boolean, boolean)"/>, so use that one when possible.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns an <see cref="UserAccountCollection"/> containing a list of roles the user has permission to view.
	/// </returns>
	/// <overloads>
	/// Gets a collection of users the current user has permission to view.
	/// </overloads>
	public static UserAccountCollection getUsersCurrentUserCanView(long galleryId) throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return getUsersCurrentUserCanView(isCurrentUserSysAdministrator(), isCurrentUserGalleryAdministrator(galleryId));
	}

	/// <summary>
	/// Gets a collection of users the current user has permission to view. Users who have administer site permission can view all users,
	/// as can gallery administrators when the application setting <see cref="IAppSetting.AllowGalleryAdminToViewAllUsersAndRoles" /> is true. When 
	/// the setting is false, gallery admins can only view users in galleries they have gallery admin permission in. Note that
	/// a user may be able to view a user but not update it. This can happen when the user belongs to roles that are associated with
	/// galleries the current user is not an admin for. The users may be returned from a cache. Guaranteed to not return null.
	/// This overload is faster than <see cref="GetUsersCurrentUserCanView(int)" />, so use this one when possible.
	/// </summary>
	/// <param name="userIsSiteAdmin">If set to <c>true</c>, the currently logged on user is a site administrator.</param>
	/// <param name="userIsGalleryAdmin">If set to <c>true</c>, the currently logged on user is a gallery administrator for the current gallery.</param>
	/// <returns>
	/// Returns an <see cref="UserAccountCollection"/> containing a list of roles the user has permission to view.
	/// </returns>
	public static UserAccountCollection getUsersCurrentUserCanView(boolean userIsSiteAdmin, boolean userIsGalleryAdmin) throws InvalidMDSRoleException{
		if (userIsSiteAdmin){
			return UserUtils.getAllUsers();
		}else if (userIsGalleryAdmin){
			// See if we have a list in the cache. If not, generate it and add to cache.
			ConcurrentHashMap<String, UserAccountCollection> usersCache = (ConcurrentHashMap<String, UserAccountCollection>)CacheUtils.get(CacheItem.UsersCurrentUserCanView);

			UserAccountCollection users;
			String cacheKeyName = StringUtils.EMPTY;

			if (getSession() != null) {
				cacheKeyName = getCacheKeyNameForUsersCurrentUserCanView(UserUtils.getLoginName());
	
				if ((usersCache != null) && ((users = usersCache.get(cacheKeyName)) != null)){
					return users;
				}
			}

			// Nothing in the cache. Calculate it - this is processor intensive when there are many users and/or roles.
			users = determineUsersCurrentUserCanView(userIsSiteAdmin, userIsGalleryAdmin);

			// Add to the cache before returning.
			if (usersCache == null)	{
				usersCache = new ConcurrentHashMap<String, UserAccountCollection>();
			}

			// Add to the cache, but only if we have access to the session ID.
			if (getSession() != null){
				synchronized (usersCache)
				{
					if (!usersCache.containsKey(cacheKeyName)){
						usersCache.put(cacheKeyName, users);
					}
				}
				CacheUtils.put(CacheItem.UsersCurrentUserCanView, usersCache);
			}

			return users;
		}

		return new UserAccountCollection();
	}

	/// <summary>
	/// Gets a data entity containing information about the specified <paramref name="userName" /> or the current user
	/// if <paramref name="userName" /> is null or empty. A <see cref="GallerySecurityException" /> is thrown if the 
	/// current user does not have view and edit permission to the requested user. The instance can be JSON-parsed and sent to the
	/// browser.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <param name="galleryId">The gallery ID. Optional parameter - But note that when not specified, the <see cref="User.UserAlbumId" />
	/// property is assigned to zero, regardless of its actual value.</param>
	/// <returns>Returns <see cref="UserRest" /> object containing information about the current user.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have permission to view and edit the user.</exception>
	/// <exception cref="InvalidUserException">Thrown when the requested user does not exist.</exception>
	/// <exception cref="InvalidGalleryException">Thrown when the gallery ID does not represent an existing gallery.</exception>
	public static UserRest getUserEntity(String userName, long galleryId) throws InvalidUserException, InvalidGalleryException, GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException{
		CMUtils.loadGallery(galleryId); // Throws ex if gallery ID is not valid

		if (StringUtils.isBlank(userName)) {
			UserRest userRest = new UserRest();
			userRest.IsNew = true; 
			userRest.GalleryId = galleryId; 
			userRest.Roles = new String[0];
			
			return userRest;
		}

		UserAccount user = getUserAccount(userName, false);

		if (user == null){
			if (isCurrentUserSysAdministrator() || isCurrentUserGalleryAdministrator(galleryId))
				throw new InvalidUserException(MessageFormat.format("User '{0}' does not exist", userName));
			else
				throw new GallerySecurityException("Insufficient permission to view user."); // Throw to avoid giving non-admin clues about existence of user
		}else if (!userCanViewAndEditUser(user))
			throw new GallerySecurityException("Insufficient permission to view user.");

		Pair<Boolean, Boolean> userAddPerms = SecurityGuard.getUserAddObjectPermissions(RoleUtils.getMDSRolesForUser(), galleryId);

		UserRest userRest = new UserRest();
		userRest.UserName = user.getUserName();
		userRest.Comment = user.getComment();
		userRest.Email = user.getEmail();
		userRest.IsApproved = user.getIsApproved();
		userRest.IsAuthenticated = isAuthenticated();
		userRest.CanAddAlbumToAtLeastOneAlbum = userAddPerms.getLeft();
		userRest.CanAddContentToAtLeastOneAlbum = userAddPerms.getRight();
		userRest.EnableUserAlbum = ProfileUtils.getProfile(user.getUserName()).getGalleryProfile(galleryId).getEnableUserAlbum();
		userRest.UserAlbumId = Math.max((galleryId > Long.MIN_VALUE ? getUserAlbumId(user.getUserName(), galleryId) : 0), 0); // Returns 0 for no user album
		userRest.GalleryId = galleryId;
		userRest.CreationDate = user.getCreationDate();
		userRest.IsLockedOut = user.getIsLockedOut();
		userRest.LastActivityDate = user.getLastActivityDate();
		userRest.LastLoginDate = user.getLastLoginDate();
		userRest.LastPasswordChangedDate = user.getLastPasswordChangedDate();
		userRest.Roles = RoleUtils.getRoleNamesForUser(userName);
		userRest.Password = null;
		userRest.PasswordResetRequested = null;
		userRest.PasswordChangeRequested = null;
		userRest.NotifyUserOnPasswordChange = null;

		return userRest;
	}

	

	/// <overloads>
	/// Persist the user to the data store.
	/// </overloads>
	/// <summary>
	/// Persist the <paramref name="user" /> to the data store. If a password reset is being requested, the new password is 
	/// assigned to <paramref name="newPassword" />.
	/// </summary>
	/// <param name="user">The user to save.</param>
	/// <param name="newPassword">The value of the newly reset password. Assigned only when <see cref="UserRest.PasswordResetRequested" />
	/// is <c>true</c>; will be null in all other cases.</param>
	/// <exception cref="System.ArgumentNullException">user</exception>
	/// <exception cref="System.ArgumentOutOfRangeException">user;The GalleryId property of the user parameter was null.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	/// <exception cref="InvalidUserException">Thrown when the e-mail address is not valid.</exception>
	public static void saveUser(UserRest user, String newPassword) throws UnsupportedContentObjectTypeException, InvalidMDSRoleException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidGalleryException, WebException, CannotDeleteAlbumException, InvalidUserException{
		if (user == null)
			throw new ArgumentNullException("user");

		if (user.GalleryId == null)
			throw new ArgumentOutOfRangeException("user", "The GalleryId property of the user parameter was null.");

		if (user.Roles == null)
			throw new ArgumentOutOfRangeException("user", "The Roles property of the user parameter was null.");

		UserAccount userAccount = toUserAccount(user);

		if (userAccount == null)
			throw new GallerySecurityException();

		saveUser(userAccount, user.Roles);

		saveProfileProperties(user);

		//HandlePasswordUpdateRequest(user, out newPassword);

		//HelperFunctions.PurgeCache();
	}

	/// <summary>
	/// Persist the <paramref name="user" /> to the data store.
	/// </summary>
	/// <param name="user">The user to save.</param>
	public static void saveUser(UserAccount user){
		UserUtils.updateUser(user);
	}

	/// <summary>
	/// Persist the <paramref name="user"/> to the data store, including associating the specified roles with the user. The user is
	/// automatically removed from any other roles they may be a member of. Prior to saving, validation is performed and a 
	/// <see cref="GallerySecurityException"/> is thrown if a business rule would be violated.
	/// </summary>
	/// <param name="user">The user to save.</param>
	/// <param name="roles">The roles to associate with the user.</param>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	/// <exception cref="InvalidUserException">Thrown when the e-mail address is not valid.</exception>
	public static void saveUser(UserAccount user, String[] roles) throws UnsupportedContentObjectTypeException, InvalidMDSRoleException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidGalleryException, WebException, InvalidUserException{
		if (user == null)
			throw new ArgumentNullException("user");

		if (roles == null)
			throw new ArgumentNullException("roles");

		validateSaveUser(user, roles);

		UserUtils.updateUser(user);

		String[] rolesForUser = RoleUtils.getRoleNamesForUser(user.getUserName());
		String[] rolesToAdd = Arrays.asList(roles).stream().filter(r -> !ArrayUtils.contains(rolesForUser, r)).toArray(String[]::new);
		String[] rolesToRemove = Arrays.asList(rolesForUser).stream().filter(r -> !ArrayUtils.contains(roles, r)).toArray(String[]::new);

		RoleUtils.addUserToRoles(user.getUserName(), rolesToAdd);
		RoleUtils.removeUserFromRoles(user.getUserName(), rolesToRemove);

		boolean addingOrDeletingRoles = ((rolesToAdd.length > 0) || (rolesToRemove.length > 0));

		if (addingOrDeletingRoles){
			CacheUtils.remove(CacheItem.MDSRoles);
		}
	}
	
	/// <summary>
	/// Removes a user from the membership data source.
	/// </summary>
	/// <param name="userName">The name of the user to delete.</param>
	/// <returns><c>true</c> if the user was successfully deleted; otherwise, <c>false</c>.</returns>
	public static boolean deleteUser(String userName){
		//return MembershipMds.DeleteUser(userName, true);
		//User user = userManager.getUserByUsername(userName);
		//userManager.removeUser(user);
		UserManager userManager = SpringContextHolder.getBean(UserManager.class);
		try {
			userManager.removeUser(userName);
			return true;
		}catch(Exception ex) {
			return false;
		}
		
	}

	/// <summary>
	/// Contains functionality that must execute after a user has logged on. Specifically, roles are cleared from the cache
	/// and, if user albums are enabled, the user's personal album is validated. Developers integrating MDS System into
	/// their applications should call this method after they have authenticated a user. User must be logged on by the
	/// time this method is called. For example, one can call this method in the LoggedIn event of the ASP.NET Login control.
	/// </summary>
	/// <param name="galleryId">The gallery ID for the gallery where the user album is to be validated. This value is required.</param>
	/// <param name="userName">Name of the user that has logged on.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="galleryId"/> is <see cref="Integer.MIN_VALUE" />.</exception>
	public static void userLoggedOn(String userName, long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		// NOTE: If modifying this function to use galleryId in a place other than the ValidateUserAlbum function, be sure to 
		// update the XML comment for this parameter.
		RoleUtils.removeRolesFromCache();

		// Store the user name and the fact that user is authenticated. Ideally we would not do this and just use
		// User.Identity.Name and User.Identity.IsAuthenticated, but those won't be assigned by ASP.NET until the 
		// next page load.
		//logon();

		validateUserAlbum(userName, galleryId);
	}

	/// <summary>
	/// Contains functionality that must execute after a user has logged off. Specifically, roles are cleared from the cache.
	/// Developers integrating MDS System into their applications should call this method after a user has signed out. 
	/// User must be already be logged off by the time this method is called. For example, one can call this method in the 
	/// LoggedOut event of the ASP.NET LoginStatus control.
	/// </summary>
	public static void userLoggedOff(){
		RoleUtils.removeRolesFromCache();

		// Clear the user name and the fact that user is not authenticated. Ideally we would not do this and just use
		// User.Identity.Name and User.Identity.IsAuthenticated, but those won't be assigned by ASP.NET until the 
		// next page load.
		logout();
	}

	/// <overloads>
	/// Create a new user in the Membership data store.
	/// </overloads>
	/// <summary>
	/// Creates the new user having the properties specified in <paramref name="user" />. Note that only the username, email,
	/// password, and roles are persisted. To save other properties, call <see cref="SaveUser(UserRest, out String)" /> after executing this function.
	/// </summary>
	/// <param name="user">The user.</param>
	/// <returns>An instance of <see cref="UserAccount" />.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="MembershipCreateUserException">Thrown when an error occurs during account creation. Check the StatusCode
	/// property for a MembershipCreateStatus value.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when the <see cref="UserRest.GalleryId" /> property of the <paramref name="user" />
	///  parameter is null.</exception>
	public static UserAccount createUser(UserRest user) throws AddressException, GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidUserException, IOException, InvalidGalleryException	{
		if (user == null)
			throw new ArgumentNullException("user");

		if (user.GalleryId == null)
			throw new ArgumentOutOfRangeException("user", "The GalleryId property of the user parameter was null.");

		return createUser(user.UserName, user.Password, user.Email, user.Roles, false, user.GalleryId);
	}

	/// <summary>
	/// Creates a new account in the membership system with the specified <paramref name="userName"/>, <paramref name="password"/>,
	/// <paramref name="email"/>, and belonging to the specified <paramref name="roles"/>. If required, it sends a verification
	/// e-mail to the user, sends an e-mail notification to admins, and creates a user album. The account will be disabled when
	/// <paramref name="isSelfRegistration"/> is <c>true</c> and either the system option 
	/// <see cref="GallerySettings.RequireEmailValidationForSelfRegisteredUser" /> or 
	/// <see cref="GallerySettings.RequireApprovalForSelfRegisteredUser" /> is enabled.
	/// </summary>
	/// <param name="userName">Account name of the user. Cannot be null or empty.</param>
	/// <param name="password">The password for the user. Cannot be null or empty.</param>
	/// <param name="email">The email associated with the user. Required when <paramref name="isSelfRegistration"/> is true
	/// and email verification is enabled.</param>
	/// <param name="roles">The names of the roles to assign to the user. The roles must already exist. If null or empty, no
	/// roles are assigned to the user.</param>
	/// <param name="isSelfRegistration">Indicates when the user is creating his or her own account. Set to false when an
	/// administrator creates an account.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns the newly created user.</returns>
	/// <exception cref="MembershipCreateUserException">Thrown when an error occurs during account creation. Check the StatusCode
	/// property for a MembershipCreateStatus value.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userName" /> or <paramref name="password" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when <paramref name="userName" /> or <paramref name="password" /> is an empty String.</exception>
	public static UserAccount createUser(String userName, String password, String email, String[] roles, boolean isSelfRegistration, long galleryId) throws AddressException, GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidUserException, IOException, InvalidGalleryException{
		//#region Validation
		if (userName == null)
			throw new ArgumentNullException("userName");

		if (password == null)
			throw new ArgumentNullException("password");

		if (StringUtils.isBlank(userName))
			throw new ArgumentException("The parameter cannot be an empty String.", "userName");

		if (StringUtils.isBlank(password))
			throw new ArgumentException("The parameter cannot be an empty String.", "password");

		if ((StringUtils.isBlank(email)) && (HelperFunctions.isValidEmail(userName))){
			// No email address was specified, but the user name happens to be in the form of an email address,
			// so let's set the email property to the user name.
			email = userName;
		}

		//#endregion

		GallerySettings gallerySettings = CMUtils.loadGallerySetting(galleryId);

		// Step 1: Create the user. Any number of exceptions may occur; we'll let the caller deal with them.
		UserAccount user = createUser(userName, password, email);

		// Step 2: If this is a self-registered account and email verification is enabled or admin approval is required,
		// disable it. It will be approved when the user validates the email or the admin gives approval.
		if (isSelfRegistration)	{
			if (gallerySettings.getRequireEmailValidationForSelfRegisteredUser() || gallerySettings.getRequireApprovalForSelfRegisteredUser())	{
				user.setIsApproved(false);
				updateUser(user);
			}
		}

		// Step 3: Verify no business rules are being violated by the logged-on user creating an account. We skip this verification
		// for self registrations, because there isn't a logged-on user.
		if (!isSelfRegistration){
			validateSaveUser(user, roles);
		}

		// Step 4: Add user to roles.
		if ((roles != null) && (roles.length > 0)){
			for (String role : roles){
				RoleUtils.addUserToRole(userName, role);
			}
		}

		// Step 5: Notify admins that an account was created.
		notifyAdminsOfNewlyCreatedAccount(user, isSelfRegistration, false, galleryId);

		// Step 6: Send user a welcome message or a verification link.
		if (HelperFunctions.isValidEmail(user.getEmail())){
			notifyUserOfNewlyCreatedAccount(user, galleryId);
		}else if (isSelfRegistration && gallerySettings.getRequireEmailValidationForSelfRegisteredUser()){
			// Invalid email, but we need one to send the email verification. Throw error.
			throw new AddressException("Invalid Email");
		}

		HelperFunctions.purgeCache();

		return user;
	}

	/// <summary>
	/// Delete the user from the membership system. In addition, remove the user from any roles. If a role is an ownership role,
	/// then delete it if the user is the only member. Remove the user from ownership of any albums, and delete the user's
	/// personal album, if user albums are enabled.
	/// </summary>
	/// <param name="userName">Name of the user to be deleted.</param>
	/// <param name="preventDeletingLoggedOnUser">If set to <c>true</c>, throw a <see cref="WebException"/> if attempting
	/// to delete the currently logged on user.</param>
	/// <exception cref="WebException">Thrown when the user cannot be deleted because doing so violates one of the business rules.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be deleted because doing so violates one of the business rules.</exception>
	public static void deleteMDSUser(String userName, boolean preventDeletingLoggedOnUser) throws GallerySecurityException, UnsupportedContentObjectTypeException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, InvalidGalleryException, WebException, CannotDeleteAlbumException{
		if (StringUtils.isBlank(userName))
			return;

		validateDeleteUser(userName, preventDeletingLoggedOnUser, true);

		for (GalleryBo gallery : CMUtils.loadGalleries()){
			deleteUserAlbum(userName, gallery.getGalleryId());
		}


		ProfileUtils.deleteProfileForUser(userName);

		deleteUser(userName);

		HelperFunctions.purgeCache();
	}

	///// <summary>
	///// Gets a <see cref="System.Data.DataTable"/> named Users with a single String column named UserName that contains the user 
	///// names of all the members as returned by GetAllUsers(). Data may be returned from cache.
	///// </summary>
	///// <returns>Returns a <see cref="System.Data.DataTable"/> containing the user names of all the current users.</returns>
	//public static DataTable GetUserNames()
	//{
	//  DataTable usersCache = (DataTable)HelperFunctions.getCache(CacheItem.Users);

	//  if (usersCache == null)
	//  {
	//    usersCache = new DataTable("Users");
	//    usersCache.Columns.Add(new DataColumn("UserName", typeof(String)));
	//    for (UserAccount user in GetAllUsers())
	//    {
	//      DataRow dr = usersCache.NewRow();
	//      dr[0] = user.UserName;
	//      usersCache.Rows.Add(dr);
	//    }

	//    HelperFunctions.SetCache(CacheItem.Users, usersCache);
	//  }

	//  return usersCache;
	//}

	/// <overloads>
	/// Gets the personal album for a user.
	/// </overloads>
	/// <summary>
	/// Gets the album for the current user's personal album and <paramref name="galleryId" /> (that is, get the 
	/// album that was created when the user's account was created). The album is created if it does not exist. 
	/// If user albums are disabled or the user has disabled their own album, this function returns null. It also 
	/// returns null if the UserAlbumId property is not found in the profile (this should not typically occur).
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns the album for the current user's personal album.</returns>
	public static AlbumBo getUserAlbum(long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		return getUserAlbum(UserUtils.getLoginName(), galleryId);
	}

	/// <summary>
	/// Gets the personal album for the specified <paramref name="userName"/> and <paramref name="galleryId" /> 
	/// (that is, get the album that was created when the user's account was created). The album is created if it 
	/// does not exist. If user albums are disabled or the user has disabled their own album, this function returns 
	/// null. It also returns null if the UserAlbumId property is not found in the profile (this should not typically occur).
	/// </summary>
	/// <param name="userName">The account name for the user.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns the personal album for the specified <paramref name="userName"/>.
	/// </returns>
	public static AlbumBo getUserAlbum(String userName, long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		return validateUserAlbum(userName, galleryId);
	}

	/// <summary>
	/// Gets the ID of the album for the specified user's personal album (that is, this is the album that was created when the
	/// user's account was created). If user albums are disabled or the UserAlbumId property is not found in the profile,
	/// this function returns Integer.MIN_VALUE. This function executes faster than <see cref="GetUserAlbum(int)"/> and 
	/// <see cref="GetUserAlbum(String, int)"/> but it does not validate that the album exists.
	/// </summary>
	/// <param name="userName">The account name for the user.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns the ID of the album for the current user's personal album.
	/// </returns>
	public static long getUserAlbumId(String userName, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		long albumId = Long.MIN_VALUE;

		if (!CMUtils.loadGallerySetting(galleryId).getEnableUserAlbum())
			return albumId;

		long tmpAlbumId = ProfileUtils.getProfileForGallery(userName, galleryId).getUserAlbumId();
		albumId = (tmpAlbumId > 0 ? tmpAlbumId : albumId);

		return albumId;
	}

	/// <summary>
	/// Verifies the user album for the specified <paramref name="userName">user</paramref> exists if it is supposed to exist
	/// (creating it if necessary), or does not exist if not (that is, deleting it if necessary). Returns a reference to the user
	/// album if a user album exists or has just been created; otherwise returns null. Also returns null if user albums are
	/// disabled at the application level or <see cref="GallerySettings.UserAlbumParentAlbumId" /> does not match an existing album.
	/// A user album is created if user albums are enabled but none for the user exists. If user albums are enabled at the
	/// application level but the user has disabled them in his profile, the album is deleted if it exists.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <param name="galleryId">The gallery ID for the gallery where the user album is to be validated. This value is required.</param>
	/// <returns>
	/// Returns a reference to the user album for the specified <paramref name="userName">user</paramref>, or null
	/// if user albums are disabled or <see cref="GallerySettings.UserAlbumParentAlbumId" /> does not match an existing album.
	/// </returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name="userName"/> is null or empty.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="galleryId"/> is <see cref="Integer.MIN_VALUE" />.</exception>
	public static AlbumBo validateUserAlbum(String userName, long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		if (StringUtils.isBlank(userName))
			throw new ArgumentException("Parameter cannot be null or an empty String.", "userName");

		if (!CMUtils.loadGallerySetting(galleryId).getEnableUserAlbum())
			return null;

		if (galleryId == Long.MIN_VALUE)	{
			// If we get here then user albums are enabled but an invalid gallery ID has been passed. This function can't do 
			// its job without the ID, so throw an error.
			throw new ArgumentOutOfRangeException(MessageFormat.format("A valid gallery ID must be passed to the UserUtils.ValidateUserAlbum function when user albums are enabled. Instead, the value {0} was passed for the gallery ID.", galleryId));
		}

		boolean userAlbumExists = false;
		boolean userAlbumShouldExist = ProfileUtils.getProfileForGallery(userName, galleryId).getEnableUserAlbum();

		AlbumBo album = null;

		long albumId = getUserAlbumId(userName, galleryId);

		if (albumId > Long.MIN_VALUE){
			try{
				// Try loading the album.
				album = AlbumUtils.loadAlbumInstance(albumId, false, true);

				userAlbumExists = true;
			}catch (InvalidAlbumException ex) { }
		}

		// Delete or create if necessary. Deleting should only be needed if 
		if (userAlbumExists && !userAlbumShouldExist){
			try	{
				AlbumUtils.deleteAlbum(album);
			}catch (Exception ex){
				// Log any errors that happen but don't let them bubble up.
				AppEventLogUtils.LogError(ex, galleryId);
			}finally{
				album = null;
			}
		}else if (!userAlbumExists && userAlbumShouldExist)	{
			album = AlbumUtils.createUserAlbum(userName, galleryId);
		}

		return album;
	}

	/// <summary>
	/// Activates the account for the specified <paramref name="userName"/> and automatically logs on the user. If the
	/// admin approval system setting is enabled (RequireApprovalForSelfRegisteredUser=<c>true</c>), then record the
	/// validation in the user's comment field but do not activate the account. Instead, send the administrator(s) an
	/// e-mail notifying them of a pending account. This method is typically called after a user clicks the confirmation
	/// link in the verification e-mail after creating a new account.
	/// </summary>
	/// <param name="userName">Name of the user who has just validated his or her e-mail address.</param>
	/// <param name="galleryId">The gallery ID for the gallery where the user is being activated. This value is required.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="galleryId"/> is <see cref="Integer.MIN_VALUE" />.</exception>
	public static void uerEmailValidatedAfterCreation(String userName, long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		UserAccount user = getUserAccount(userName, true);

		notifyAdminsOfNewlyCreatedAccount(user, true, true, galleryId);

		if (!CMUtils.loadGallerySetting(galleryId).getRequireApprovalForSelfRegisteredUser())
		{
			user.setIsApproved(true);

			logOffUser();
			logOnUser(userName, galleryId);
		}

		user.setComment(I18nUtils.getMessage("CreateAccount_Verification_Comment_Text", user.getEmail(), DateUtils.Now()));

		updateUser(user);
	}

	/// <summary>
	/// Logs off the current user.
	/// </summary>
	public static void logOffUser()	{
		//FormsAuthentication.SignOut();

		userLoggedOff();
	}

	/// <overloads>
	/// Sets an authentication cookie for the specified user so that the user is considered logged on by the application. This
	/// function does not authenticate the user; the calling function must perform that function or otherwise guarantee that it
	/// is appropriate to log on the user.
	/// </overloads>
	/// <summary>
	/// Logs on the specified <paramref name="userName"/>.
	/// </summary>
	/// <param name="userName">The username for the user to log on.</param>
	public static void logOnUser(String userName) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		for (GalleryBo gallery : CMUtils.loadGalleries()){
			logOnUser(userName, gallery.getGalleryId());
		}
	}

	/// <summary>
	/// Sets an authentication cookie for the specified <paramref name="userName"/> so that the user is considered logged on by
	/// the application. This function does not authenticate the user; the calling function must perform that function or 
	/// otherwise guarantee that it is appropriate to log on the user.
	/// </summary>
	/// <param name="userName">The username for the user to log on.</param>
	/// <param name="galleryId">The gallery ID for the gallery where the user album is to be validated. This value is required.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="galleryId"/> is <see cref="Integer.MIN_VALUE" />.</exception>
	public static void logOnUser(String userName, long galleryId) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		//FormsAuthentication.SetAuthCookie(userName, false);

		userLoggedOn(userName, galleryId);
	}
	

	/// <summary>
	/// Get a list of galleries the current user can administer. Site administrators can view all galleries, while gallery
	/// administrators may have access to zero or more galleries.
	/// </summary>
	/// <returns>Returns an <see cref="GalleryBoCollection" /> containing the galleries the current user can administer.</returns>
	public static GalleryBoCollection getGalleriesCurrentUserCanAdminister() throws InvalidMDSRoleException{
		return getGalleriesUserCanAdminister(UserUtils.getLoginName());
	}

	/// <summary>
	/// Get a list of galleries the specified <paramref name="userName"/> can administer. Site administrators can view all
	/// galleries, while gallery administrators may have access to zero or more galleries.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns>
	/// Returns an <see cref="GalleryBoCollection"/> containing the galleries the current user can administer.
	/// </returns>
	public static GalleryBoCollection getGalleriesUserCanAdminister(String userName) throws InvalidMDSRoleException{
		GalleryBoCollection adminGalleries = new GalleryBoCollection();
		for (MDSRole role : RoleUtils.getMDSRolesForUser(userName))	{
			if (role.getAllowAdministerSite()){
				return CMUtils.loadGalleries();
			}else if (role.getAllowAdministerGallery()){
				for (GalleryBo gallery : role.getGalleries()){
					if (!adminGalleries.contains(gallery)){
						adminGalleries.add(gallery);
					}
				}
			}
		}

		return adminGalleries;
	}

	/// <summary>
	/// Gets a collection of all the galleries the specified <paramref name="userName" /> has access to.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns>Returns an <see cref="GalleryBoCollection" /> of all the galleries the specified <paramref name="userName" /> has access to.</returns>
	public static GalleryBoCollection getGalleriesForUser(String userName) throws InvalidMDSRoleException{
		GalleryBoCollection galleries = new GalleryBoCollection();

		for (MDSRole role : RoleUtils.getMDSRolesForUser(userName))	{
			for (GalleryBo gallery : role.getGalleries()){
				if (!galleries.contains(gallery)){
					galleries.add(gallery);
				}
			}
		}

		return galleries;
	}

	/// <summary>
	/// Validates the logged on user has permission to save the specified <paramref name="userToSave"/> and to add/remove the user 
	/// to/from the specified <paramref name="roles"/>. Throw a <see cref="GallerySecurityException"/> if user is not authorized.
	/// This method assumes the logged on user is a site administrator or gallery administrator but does not verify it.
	/// </summary>
	/// <param name="userToSave">The user to save. The only property that must be specified is <see cref="UserAccount.getUserName()" />.</param>
	/// <param name="roles">The roles to be associated with the user.</param>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	public static void validateLoggedOnUserHasPermissionToSaveUser(UserAccount userToSave, String[] roles) throws InvalidMDSRoleException, GallerySecurityException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		if (roles == null)
			throw new ArgumentNullException("roles");

		String[] rolesForUser = RoleUtils.getRoleNamesForUser(userToSave.getUserName());
		List<String> rolesToAdd = Arrays.asList(roles).stream().filter(r -> !ArrayUtils.contains(rolesForUser, r)).collect(Collectors.toList());
		List<String> rolesToRemove = Arrays.asList(rolesForUser).stream().filter(r -> !ArrayUtils.contains(roles, r)).collect(Collectors.toList());

		// Enforces the following rules:
		// 1. A user with site administration permission has no restrictions. Subsequent rules do not apply.
		// 2. Gallery admin is not allowed to add admin site permission to any user or update any user that has site admin permission.
		// 3. Gallery admin cannot add or remove a user to/from a role associated with other galleries, UNLESS he is also a gallery admin
		//    to those galleries.
		// 4. NOT ENFORCED: If user to be updated is a member of roles that apply to other galleries, Gallery admin must be a gallery admin 
		//    in every one of those galleries. Not enforced because this is considered acceptable behavior.

		if (isCurrentUserSysAdministrator())
			return;

		verifyGalleryAdminIsNotUpdatingUserWithAdminSitePermission(userToSave, rolesToAdd);

		verifyGalleryAdminCanAddOrRemoveRolesForUser(rolesToAdd, rolesToRemove);

		//#region RULE 4 (Not enforced)
		// RULE 4: Gallery admin can update user only when he is a gallery admin in every gallery the user to be updated is a member of.

		//// Step 1: Get a list of galleries the user to be updated is associated with.
		//GalleryBoCollection userGalleries = new GalleryCollection();
		//for (MDSRole role : RoleUtils.getMDSRolesForUser(userToSave.getUserName()))
		//{
		//  for (GalleryBo gallery : role.getGalleries())
		//  {
		//    if (!userGalleries.Contains(gallery))
		//    {
		//      userGalleries.Add(gallery);
		//    }
		//  }
		//}

		//// Step 2: Validate that the current user is a gallery admin for every gallery the user to be updated is a member of.
		//for (GalleryBo userGallery : userGalleries)
		//{
		//  if (!adminGalleries.Contains(userGallery))
		//  {
		//    throw new GallerySecurityException("You are attempting to save changes to a user that affects multiple galleries, including at least one gallery you do not have permission to administer. To edit this user, you must be a gallery administrator in every gallery this user is a member of.");
		//  }
		//}
		//#endregion
	}

	/// <summary>
	/// Verifies that the e-mail address for the <paramref name="user" /> conforms to the expected format. No action is
	/// taken if <see cref="UserRest.Email" /> is null or empty.
	/// </summary>
	/// <param name="user">The user to validate.</param>
	/// <exception cref="InvalidUserException">Thrown when the e-mail address is not valid.</exception>
	private static void validateEmail(UserAccount user) throws InvalidUserException{
		if (user == null)
			throw new ArgumentNullException("user");

		if (!StringUtils.isBlank(user.getEmail()) && !HelperFunctions.isValidEmail(user.getEmail())){
			throw new InvalidUserException("E-mail is not valid.");
		}
	}

	//#endregion

	//#region Private Static Methods
	/// <summary>
	/// Adds a new user with the specified e-mail address to the data store.
	/// </summary>
	/// <param name="userName">The user name for the new user.</param>
	/// <param name="password">The password for the new user.</param>
	/// <param name="email">The email for the new user.</param>
	/// <returns>Returns a new user with the specified e-mail address to the data store.</returns>
	private static UserAccount createUser(String userName, String password, String email){
		// This function is a re-implementation of the System.Web.Security.Membership.CreateUser method. We can't call it directly
		// because it uses the default provider, and we might be using a named provider.
		/*MembershipCreateStatus status;
		MembershipUser user = MembershipMds.CreateUser(userName, password, email, null, null, true, null, out status);
		if (user == null)
		{
			throw new MembershipCreateUserException(status);
		}*/
		User user=new User();
		user.setUsername(userName);
		user.setPassword(password);
		user.setEmail(email);

		return toUserAccount(user);
	}

	/// <summary>
	/// Send an e-mail to the users that are subscribed to new account notifications. These are specified in the
	/// <see cref="GallerySettings.UsersToNotifyWhenAccountIsCreated" /> configuration setting. If 
	/// <see cref="GallerySettings.RequireEmailValidationForSelfRegisteredUser" /> is enabled, do not send an e-mail at this time. 
	/// Instead, it is sent when the user clicks the confirmation link in the e-mail.
	/// </summary>
	/// <param name="user">An instance of <see cref="UserAccount"/> that represents the newly created account.</param>
	/// <param name="isSelfRegistration">Indicates when the user is creating his or her own account. Set to false when an
	/// administrator creates an account.</param>
	/// <param name="isEmailVerified">If set to <c>true</c> the e-mail has been verified to be a valid, active e-mail address.</param>
	/// <param name="galleryId">The gallery ID storing the e-mail configuration information and the list of users to notify.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	private static void notifyAdminsOfNewlyCreatedAccount(UserAccount user, boolean isSelfRegistration, boolean isEmailVerified, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (user == null)
			throw new ArgumentNullException("user");

		GallerySettings gallerySettings = CMUtils.loadGallerySetting(galleryId);

		if (isSelfRegistration && !isEmailVerified && gallerySettings.getRequireEmailValidationForSelfRegisteredUser()){
			return;
		}

		/*EmailTemplate emailTemplate;
		if (isSelfRegistration && gallerySettings.getRequireApprovalForSelfRegisteredUser())	{
			emailTemplate = EmailUtils.getEmailTemplate(EmailTemplateForm.AdminNotificationAccountCreatedRequiresApproval, user);
		}else{
			emailTemplate = EmailUtils.getEmailTemplate(EmailTemplateForm.AdminNotificationAccountCreated, user);
		}*/

		/*for (UserAccount userToNotify : gallerySettings.getUsersToNotifyWhenAccountIsCreated()){
			if (!StringUtils.isBlank(userToNotify.Email)){
				MailAddress admin = new MailAddress(userToNotify.Email, userToNotify.getUserName());
				try	{
					EmailUtils.SendEmail(admin, emailTemplate.Subject, emailTemplate.Body, galleryId);
				}catch (WebException ex){
					AppEventLogUtils.LogError(ex);
				}catch (SmtpException ex){
					AppEventLogUtils.LogError(ex);
				}
			}
		}*/
	}

	/// <summary>
	/// Send an e-mail to the user associated with the new account. This will be a verification e-mail if e-mail verification
	/// is enabled; otherwise it is a welcome message. The calling method should ensure that the <paramref name="user"/>
	/// has a valid e-mail configured before invoking this function.
	/// </summary>
	/// <param name="user">An instance of <see cref="UserAccount"/> that represents the newly created account.</param>
	/// <param name="galleryId">The gallery ID. This specifies which gallery to use to look up configuration settings.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	private static void notifyUserOfNewlyCreatedAccount(UserAccount user, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (user == null)
			throw new ArgumentNullException("user");

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		boolean enableEmailVerification = gallerySetting.getRequireEmailValidationForSelfRegisteredUser();
		boolean requireAdminApproval = gallerySetting.getRequireApprovalForSelfRegisteredUser();

		/*if (enableEmailVerification){
			EmailUtils.sendNotificationEmail(user, EmailTemplateForm.UserNotificationAccountCreatedNeedsVerification);
		}else if (requireAdminApproval)	{
			EmailUtils.sendNotificationEmail(user, EmailTemplateForm.UserNotificationAccountCreatedNeedsApproval);
		}else{
			EmailUtils.sendNotificationEmail(user, EmailTemplateForm.UserNotificationAccountCreated);
		}*/
	}

	/// <summary>
	/// Throws an exception if the user cannot be deleted, such as when trying to delete his or her own account, or when deleting
	/// the only account with admin permission.
	/// </summary>
	/// <param name="userName">Name of the user to delete.</param>
	/// <param name="preventDeletingLoggedOnUser">If set to <c>true</c>, throw a <see cref="GallerySecurityException"/> if attempting
	/// to delete the currently logged on user.</param>
	/// <param name="preventDeletingLastAdminAccount">If set to <c>true</c> throw a <see cref="GallerySecurityException"/> if attempting
	/// to delete the last user with <see cref="SecurityActions.AdministerSite" /> permission. When false, do not perform this check. It does not matter
	/// whether the user to delete is actually an administrator.</param>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be deleted because doing so violates one of the business rules.</exception>
	private static void validateDeleteUser(String userName, boolean preventDeletingLoggedOnUser, boolean preventDeletingLastAdminAccount) throws GallerySecurityException, InvalidMDSRoleException{
		if (preventDeletingLoggedOnUser){
			// Don't let user delete their own account.
			if (userName.equalsIgnoreCase(UserUtils.getLoginName())){
				throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Users_Cannot_Delete_User_Msg"));
			}
		}

		if (preventDeletingLastAdminAccount){
			if (!doesAtLeastOneOtherSiteAdminExist(userName)){
				if (!doesAtLeastOneOtherGalleryAdminExist(userName)){
					throw new GallerySecurityException("You are attempting to delete the only user with permission to administer a gallery or site. If you want to delete this account, first assign another account to a role with administrative permission.");
				}
			}
		}

		// User can delete account only if he is a site admin or a gallery admin in every gallery this user can access.
		GalleryBoCollection adminGalleries = getGalleriesCurrentUserCanAdminister();

		if (adminGalleries.size() > 0){ // Only continue when user is an admin for at least one gallery. This allows regular users to delete their own account.
			for (GalleryBo gallery : getGalleriesForUser(userName))	{
				if (!adminGalleries.contains(gallery)){
					throw new GallerySecurityException(MessageFormat.format("The user '{0}' has access to a gallery (Gallery ID = {1}) that you are not an administrator for. To delete a user, one of the following must be true: (1) you are a site administrator, or (2) you are a gallery administrator in every gallery the user has access to.", userName, gallery.getGalleryId()));
				}
			}
		}
	}

	/// <summary>
	/// If user is a gallery admin, verify at least one other user is a gallery admin for each gallery. If user is not a gallery 
	/// admin for any gallery, return <c>true</c> without actually verifying that each that each gallery has an admin, since it
	/// is reasonable to assume it does (and even if it didn't, that shouldn't prevent us from deleting this user).
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns><c>true</c> if at least one user besides <paramref name="userName" /> is a gallery admin for each gallery;
	/// otherwise <c>false</c>.</returns>
	private static boolean doesAtLeastOneOtherGalleryAdminExist(String userName) throws InvalidMDSRoleException{
		boolean atLeastOneOtherAdminExists = false;

		GalleryBoCollection galleriesUserCanAdminister = UserUtils.getGalleriesUserCanAdminister(userName);

		if (galleriesUserCanAdminister.isEmpty()){
			// User is not a gallery administrator, so we don't have to make sure there is another gallery administrator.
			// Besides, we can assume there is another one anyway.
			return true;
		}

		for (GalleryBo gallery : galleriesUserCanAdminister){
			// Get all the roles that have gallery admin permission to this gallery
			for (MDSRole role : RoleUtils.getMDSRolesForGallery(gallery).getRolesWithGalleryAdminPermission()){
				// Make sure at least one user besides the user specified in userName is in these roles.
				for (String userNameInRole : RoleUtils.getUsersInRole(role.getRoleId())){
					if (!userNameInRole.equalsIgnoreCase(userName))	{
						atLeastOneOtherAdminExists = true;
						break;
					}
				}

				if (atLeastOneOtherAdminExists)
					break;
			}

			if (atLeastOneOtherAdminExists)
				break;
		}

		return atLeastOneOtherAdminExists;
	}

	/// <summary>
	/// Determine if at least one other user beside <paramref name="userName" /> is a site administrator.
	/// </summary>
	/// <param name="userName">A user name.</param>
	/// <returns><c>true</c> if at least one other user beside <paramref name="userName" /> is a site administrator; otherwise <c>false</c>.</returns>
	private static boolean doesAtLeastOneOtherSiteAdminExist(String userName){
		boolean atLeastOneOtherAdminExists = false;

		for (MDSRole role : RoleUtils.getMDSRoles()){
			if (!role.getAllowAdministerSite())
				continue;

			for (String userInAdminRole : RoleUtils.getUsersInRole(role.getRoleId())){
				if (userInAdminRole != userName){
					atLeastOneOtherAdminExists = true;
					break;
				}
			}
		}
		
		return atLeastOneOtherAdminExists;
	}

	private static void deleteUserAlbum(String userName, long galleryId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException, CannotDeleteAlbumException{
		AlbumBo album = getUserAlbum(userName, galleryId);

		if (album != null)
			AlbumUtils.deleteAlbum(album);
	}

	/// <summary>
	/// Remove the user from any roles. If a role is an ownership role, then delete it if the user is the only member.
	/// Remove the user from ownership of any albums.
	/// </summary>
	/// <param name="userName">Name of the user to be deleted.</param>
	/// <remarks>The user will be specified as an owner only for those albums that belong in ownership roles, so
	/// to find all albums the user owns, we need only to loop through the user's roles and inspect the ones
	/// where the names begin with the album owner role name prefix variable.</remarks>
	private static void updateRolesAndOwnershipBeforeDeletingUser(String userName) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		List<String> rolesToDelete = new ArrayList<String>();

		String[] userRoles = RoleUtils.getRoleNamesForUser(userName);
		for (String roleName : userRoles){
			if (RoleUtils.isRoleAnAlbumOwnerRole(roleName)){
				if (RoleUtils.getUsersInRole(roleName, Long.MIN_VALUE).length <= 1)	{
					// The user we are deleting is the only user in the owner role. Mark for deletion.
					rolesToDelete.add(roleName);
				}
			}
		}

		if (userRoles.length > 0){
			for (String role : userRoles){
				RoleUtils.removeUserFromRole(userName, role);
			}
		}

		for (String roleName : rolesToDelete){
			RoleUtils.deleteSystemRole(roleName, Long.MIN_VALUE);
		}
	}

	/// <summary>
	/// Updates the properties of the <see cref="UserAccount" /> corresponding to the specified entity with the properties
	/// of the entity. The changes are not persisted to the data store. Returns null if no existing user has a username
	/// matching <see cref="UserRest.UserName" />.
	/// </summary>
	/// <param name="userEntity">The user entity.</param>
	/// <returns>An instance of <see cref="UserAccount" />, or null.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userEntity" /> is null.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when one or more properties of <paramref name="userEntity" />
	/// has an unexpected value.</exception>
	private static UserAccount toUserAccount(UserRest userEntity){
		if (userEntity == null)
			throw new ArgumentNullException("userEntity");

		if (userEntity.IsApproved == null)
			throw new ArgumentOutOfRangeException("userEntity", "The IsApproved property of the userEntity parameter was null.");

		if (userEntity.IsLockedOut == null)
			throw new ArgumentOutOfRangeException("userEntity", "The IsLockedOut property of the userEntity parameter was null.");

		UserAccount user = getUserAccount(userEntity.UserName, false);

		if (user == null)
			return null;

		user.setComment(userEntity.Comment);
		user.setEmail(userEntity.Email);
		user.setIsApproved(userEntity.IsApproved);
		user.setIsLockedOut(userEntity.IsLockedOut);

/*		if (MembershipMds.getType().ToString() == GlobalConstants.ActiveDirectoryMembershipProviderName)
		{
			// The AD provider will throw an ArgumentException during the UpdateUser method if the comment is empty,
			// so add a single space if necessary.
			if (StringUtils.isBlank(user.Comment))
				user.Comment = " ";
		}*/

		return user;
	}

	public static UserAccount toUserAccount(User user)	{
		if (user == null)
			return null;

		/*if (MembershipMds.getType().ToString() == GlobalConstants.ActiveDirectoryMembershipProviderName)
		{
			// The AD provider does not support a few properties so substitute default values for them.
			return new UserAccount(u.Comment, u.CreationDate, u.Email, u.IsApproved, u.IsLockedOut, false,
														 Date.MinValue, u.LastLockoutDate, Date.MinValue, u.LastPasswordChangedDate,
														 u.PasswordQuestion, u.ProviderName, u.ProviderUserKey, u.UserName, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
		}
		else
		{
			return new UserAccount(u.Comment, u.CreationDate, u.Email, u.IsApproved, u.IsLockedOut, u.IsOnline,
														 u.LastActivityDate, u.LastLockoutDate, u.LastLoginDate, u.LastPasswordChangedDate,
														 u.PasswordQuestion, u.ProviderName, u.ProviderUserKey, u.UserName, false, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
		}*/
		//return new UserAccount(u.getUsername());
		UserAccount loginUser = new UserAccount(user.getUsername());

        loginUser.setType(user.getUserType());
        loginUser.setId(user.getId());
        loginUser.setPassword(user.getPassword());
        loginUser.setDisplayName(user.getFullName());
        loginUser.setStatus(user.getStatus());
        //tenant id
        if (user.getTenant() != null)
        	loginUser.setTenantId(user.getTenant().getId());
        
        if (user.getOrganization() != null)
        	loginUser.setOrganizationId(user.getOrganization().getId());
        
        if (user.getStaff() != null)
        	loginUser.setStaffId(user.getStaff().getId());
        
        try {
			loginUser.setRoles(RoleUtils.getMDSRolesForRoleIds(user.getRoleIdList()));
		} catch (InvalidMDSRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return loginUser;
	}

/*	private static void UpdateMembershipUser(MembershipUser userInDb, UserAccount source){
		if (userInDb == null)
			throw new ArgumentNullException("userToUpdate");

		if (source == null)
			throw new ArgumentNullException("source");

		userInDb.Comment = source.Comment;
		userInDb.Email = source.Email;
		userInDb.IsApproved = source.IsApproved;
	}*/

	/// <summary>
	/// Updates information about a user in the data source, including unlocking the user if requested. No action is taken if
	/// an existing user in the data store is not found.
	/// </summary>
	/// <param name="userToSave">A <see cref="UserAccount"/> object that represents the user to update and the updated information for the user.</param>
	private static void updateUser(UserAccount userToSave){
		/*var userInDb = MembershipMds.getUser(userToSave.getUserName(), false);

		if (userInDb == null)
			return;

		if (UserHasBeenModified(userToSave, userInDb))
		{
			boolean userIsBeingApproved = !userInDb.IsApproved && userToSave.IsApproved;

			if (userInDb.IsLockedOut && !userToSave.IsLockedOut)
			{
				// A request is being made to unlock the user.
				UnlockUser(userToSave.getUserName());
			}

			UpdateMembershipUser(userInDb, userToSave);

			MembershipMds.UpdateUser(userInDb);

			if (userIsBeingApproved)
			{
				// Administrator is approving user. Send notification e-mail to user.
				EmailUtils.SendNotificationEmail(userToSave, EmailTemplateForm.UserNotificationAccountCreatedApprovalGiven);
			}
		}*/
	}

	/// <summary>
	/// Make sure the logged-on person has authority to save the user info and that h/she isn't doing anything stupid,
	/// like removing admin permission from his or her own account. Throws a <see cref="GallerySecurityException"/> when
	/// the action is not allowed.
	/// </summary>
	/// <param name="userToSave">The user to save.</param>
	/// <param name="roles">The roles to associate with the user.</param>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="InvalidUserException">Thrown when the e-mail address is not valid.</exception>
	private static void validateSaveUser(UserAccount userToSave, String[] roles) throws GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidUserException, IOException, InvalidGalleryException{
		if (AppSettings.getInstance().getInstallationRequested() && (GalleryUtils.getAdminUserFromInstallTextFile().UserName == userToSave.getUserName())){
			// We are creating the user specified in install.txt. Don't continue validation because it will fail 
			// if no one is logged in to the gallery or the logged on user doesn't have permission to create/edit a user.
			// This is not a security vulnerability because if the user has the ability to write a file to App_Data
			// the server is already compromised.
			return;
		}

		if (!userCanViewAndEditUser(userToSave)){
			throw new GallerySecurityException("You must be a gallery or site administrator to save changes to this user.");
		}

		if (userToSave.getUserName().equalsIgnoreCase(UserUtils.getLoginName())){
			validateUserCanSaveOwnAccount(userToSave, Arrays.asList(roles));
		}

		validateLoggedOnUserHasPermissionToSaveUser(userToSave, roles);

		validateEmail(userToSave);
	}

	/// <summary>
	/// Gets a value indicating whether the <paramref name="userToSave" /> is different than the data store's version of 
	/// the user passed in via <paramref name="userInDb" />.
	/// </summary>
	/// <param name="userToSave">The user to persist to the membership provider.</param>
	/// <param name="userInDb">The membership user as it exists in the data store.</param>
	/// <returns>A boolean indicating whether the <paramref name="userToSave" /> is different than the one stored in the
	/// membership provider.</returns>
/*	private static boolean UserHasBeenModified(UserAccount userToSave, MembershipUser userInDb)
	{
		if (userToSave == null)
			throw new ArgumentNullException("userToSave");

		if (userInDb == null)
			throw new ArgumentNullException("userInDb");

		boolean commentEqual = ((StringUtils.isBlank(userToSave.Comment) && StringUtils.isBlank(userInDb.Comment)) || userToSave.Comment == userInDb.Comment);
		boolean emailEqual = ((StringUtils.isBlank(userToSave.Email) && StringUtils.isBlank(userInDb.Email)) || userToSave.Email == userInDb.Email);
		boolean isApprovedEqual = (userToSave.IsApproved == userInDb.IsApproved);
		boolean isLockEqual = (userToSave.IsLockedOut == userInDb.IsLockedOut);

		return (!(commentEqual && emailEqual && isApprovedEqual && isLockEqual));
	}*/

	/// <summary>
	/// Validates the user can save his own account. Throws a <see cref="GallerySecurityException" /> when the action is not allowed.
	/// </summary>
	/// <param name="userToSave">The user to save.</param>
	/// <param name="roles">The roles to associate with the user.</param>
	/// <exception cref="GallerySecurityException">Thrown when the user cannot be saved because doing so would violate a business rule.</exception>
	private static void validateUserCanSaveOwnAccount(UserAccount userToSave, Collection<String> roles) throws GallerySecurityException, InvalidMDSRoleException{
		// This function should be called only when the logged on person is updating their own account. They are not allowed to 
		// revoke approval and they must remain in at least one role that has Administer Site or Administer Gallery permission.
		if (!userToSave.getIsApproved()){
			throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Users_Cannot_Revoke_Approval_Msg"));
		}

		if (!RoleUtils.getMDSRoles(roles, Long.MIN_VALUE).stream().anyMatch(role -> role.getAllowAdministerSite() || role.getAllowAdministerGallery())){
			throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Users_Cannot_Save_User_Msg"));
		}
	}

	/// <summary>
	/// Verifies that the specified <paramref name="userToSave" /> is not a site administrator or is being added to a site administrator
	/// role. Calling methods should invoke this function ONLY when the current user is a gallery administrator.
	/// </summary>
	/// <param name="userToSave">The user to save. The only property that must be specified is <see cref="UserAccount.getUserName()" />.</param>
	/// <param name="rolesToAdd">The roles to be associated with the user. Must not be null. The roles should not already be assigned to the
	/// user, although no harm is done if they are.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userToSave" /> or <paramref name="rolesToAdd" /> is null.</exception>
	private static void verifyGalleryAdminIsNotUpdatingUserWithAdminSitePermission(UserAccount userToSave, Collection<String> rolesToAdd) throws InvalidMDSRoleException, GallerySecurityException{
		if (userToSave == null)
			throw new ArgumentNullException("userToSave");

		if (rolesToAdd == null)
			throw new ArgumentNullException("rolesToAdd");

		MDSRoleCollection rolesAssignedOrBeingAssignedToUser = RoleUtils.getMDSRolesForUser(userToSave.getUserName()).copy();

		for (String roleToAdd : rolesToAdd)	{
			if (rolesAssignedOrBeingAssignedToUser.getRole(roleToAdd, Long.MIN_VALUE) == null){
				MDSRole role = RoleUtils.loadMDSRole(roleToAdd, Long.MIN_VALUE);

				if (role != null){
					rolesAssignedOrBeingAssignedToUser.add(role);
				}
			}
		}

		for (MDSRole role : rolesAssignedOrBeingAssignedToUser){
			if (role.getAllowAdministerSite()){
				throw new GallerySecurityException("You must be a site administrator to add a user to a role with Administer site permission or update an existing user who has Administer site permission. Sadly, you are just a gallery administrator.");
			}
		}
	}

	/// <summary>
	/// Verifies the current user can add or remove the specified roles to or from a user. Specifically, the user must be a gallery
	/// administrator in every gallery each role is associated with. Calling methods should invoke this function ONLY when the current 
	/// user is a gallery administrator.
	/// </summary>
	/// <param name="rolesToAdd">The roles to be associated with the user. Must not be null. The roles should not already be assigned to the
	/// user, although no harm is done if they are.</param>
	/// <param name="rolesToRemove">The roles to remove from user.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="rolesToAdd" /> or <paramref name="rolesToRemove" /> is null.</exception>
	private static void verifyGalleryAdminCanAddOrRemoveRolesForUser(Collection<String> rolesToAdd, Collection<String> rolesToRemove) throws InvalidMDSRoleException, GallerySecurityException{
		if (rolesToAdd == null)
			throw new ArgumentNullException("rolesToAdd");

		if (rolesToRemove == null)
			throw new ArgumentNullException("rolesToRemove");

		GalleryBoCollection adminGalleries = UserUtils.getGalleriesCurrentUserCanAdminister();

		List<String> rolesBeingAddedOrRemoved = new ArrayList<String>(rolesToAdd);
		rolesBeingAddedOrRemoved.addAll(rolesToRemove);

		for (String roleName : rolesBeingAddedOrRemoved){
			// Gallery admin cannot add or remove a user to/from a role associated with other galleries, UNLESS he is also a gallery admin
			// to those galleries.
			MDSRole roleToAddOrRemove = RoleUtils.loadMDSRole(roleName, Long.MIN_VALUE);

			if (roleToAddOrRemove != null){
				for (GalleryBo gallery : roleToAddOrRemove.getGalleries()){
					if (!adminGalleries.contains(gallery)){
						throw new GallerySecurityException(MessageFormat.format("You are attempting to save changes to a user that will affect multiple galleries, including at least one gallery you do not have permission to administer. Specifically, the role '{0}' applies to gallery {1}, which you are not an administrator for.", roleToAddOrRemove.getRoleName(), gallery.getGalleryId()));
					}
				}
			}
		}
	}

	/// <summary>
	/// Determine the users the currently logged on user can view.
	/// </summary>
	/// <param name="userIsSiteAdmin">If set to <c>true</c>, the currently logged on user is a site administrator.</param>
	/// <param name="userIsGalleryAdmin">If set to <c>true</c>, the currently logged on user is a gallery administrator for the current gallery.</param>
	/// <returns>Returns an <see cref="UserAccountCollection"/> containing a list of roles the user has permission to view.</returns>
	private static UserAccountCollection determineUsersCurrentUserCanView(boolean userIsSiteAdmin, boolean userIsGalleryAdmin) throws InvalidMDSRoleException	{
		//if (userIsSiteAdmin || (userIsGalleryAdmin && AppSetting.Instance.AllowGalleryAdminToViewAllUsersAndRoles))
		if (userIsSiteAdmin){ // Modify by John 14/10/2014
			return UserUtils.getAllUsers();
		}

		// Filter the accounts so that only users in galleries where
		// the current user is a gallery admin are shown.
		GalleryBoCollection adminGalleries = UserUtils.getGalleriesCurrentUserCanAdminister();

		UserAccountCollection users = new UserAccountCollection();
		for (UserAccount user : UserUtils.getAllUsers()){
			for (MDSRole role : RoleUtils.getMDSRolesForUser(user.getUserName())){
				if (role.getAllowAdministerSite() && !userIsSiteAdmin)// add by John 14/10/2014
					break;

				boolean userHasBeenAdded = false;
				for (GalleryBo gallery : role.getGalleries()){
					if (adminGalleries.contains(gallery)){
						// User belongs to a gallery that the current user is a gallery admin for. Include the account.
						users.add(user);
						userHasBeenAdded = true;
						break;
					}
				}
				if (userHasBeenAdded) break;
			}
		}
		return users;
	}

	private static String getCacheKeyNameForUsersCurrentUserCanView(String userName){
		return StringUtils.join(((WebAuthenticationDetails)getSession().getDetails()).getSessionId(), "_", userName, "_Users");
	}

	/// <summary>
	/// Determines whether the user has permission to view and edit the specified user. Determines this by checking
	/// whether the logged on user is a site administrator, the same as the user being viewed, or a gallery 
	/// administrator for at least one gallery associated with the user, or a gallery admin for ANY gallery and the 
	/// option AllowGalleryAdminToViewAllUsersAndRoles is enabled. NOTE: This function assumes the current
	/// user is a site or gallery admin, so be sure this rule is enforced at some point before persisting to
	/// the data store.
	/// </summary>
	/// <param name="user">The user to evaluate.</param>
	/// <returns><c>true</c> if the user has permission to view and edit the specified user; otherwise <c>false</c>.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	private static boolean userCanViewAndEditUser(UserAccount user) throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		if (user == null)
			throw new ArgumentNullException("user");

		if (isCurrentUserSysAdministrator()){
			return true;
		}

		if (UserUtils.getLoginName().equalsIgnoreCase(user.getUserName())){
			return true; // User can edit their own account
		}

		// Return true if any of the galleries the current user can administer is also one of the galleries the specified
		// user is associated with.
		GalleryBoCollection galleriesCurrentUserCanAdminister = getGalleriesCurrentUserCanAdminister();
		boolean userIsInGalleryCurrentUserHasAdminRightsFor = RoleUtils.getMDSRolesForUser(UserUtils.getLoginName())
			.stream().anyMatch(r -> r.getGalleries().stream().anyMatch(g->galleriesCurrentUserCanAdminister.contains(g)));

		return userIsInGalleryCurrentUserHasAdminRightsFor || (AppSettings.getInstance().getAllowGalleryAdminToViewAllUsersAndRoles() && !GalleryUtils.getGalleriesCurrentUserCanAdminister().isEmpty());
	}

	private static void saveProfileProperties(UserRest user) throws UnsupportedContentObjectTypeException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException, CannotDeleteAlbumException{
		if (user.GalleryId == null || user.GalleryId == Long.MIN_VALUE)
			return;

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(user.GalleryId);

		if (!gallerySetting.getEnableUserAlbum())
			return; // User albums are disabled system-wide, so there is nothing to save.

		// Get reference to user's album. We need to do this *before* saving the profile, because if the admin disabled the user album,
		// this method will return null after saving the profile.
		AlbumBo album = UserUtils.getUserAlbum(user.UserName, user.GalleryId);

		UserProfile userProfile = ProfileUtils.getProfile(user.UserName);
		UserGalleryProfile profile = userProfile.getGalleryProfile(user.GalleryId);

		profile.setEnableUserAlbum(user.EnableUserAlbum);

		if (!profile.getEnableUserAlbum()){
			profile.setUserAlbumId(0);
		}

		ProfileUtils.saveProfile(userProfile);

		if (!profile.getEnableUserAlbum()){
			AlbumUtils.deleteAlbum(album);
		}
	}

	//#endregion
}
