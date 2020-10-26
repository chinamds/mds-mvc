/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Role Type
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum RoleType {

    /**
     * System Administrators
     */
    sa("System Administrator"),
    /**
     * Administrators
     */
    ad("Administrator"),
    /**
     * Organization Administrators
     */
    oa("Organization Administrator"),
    /**
     * Organization users
     */
    ou("Organization User"),
    /**
     * Organization guests
     */
    og("Organization Guest"),
    /**
     * Gallery Administrators
     */
    ga("Gallery Administrator"),
    /**
     * Gallery users
     */
    gu("Gallery User"),
    /**
     * Gallery guests
     */
    gg("Gallery Guest"),
    /**
     * users
     */
    ur("User"),
	/**
     * guests
     */
    gt("Guest");

    private final String info;

    private RoleType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
    
    public static RoleType getRoleType(int index) {
    	if (index < 0 || index >= RoleType.values().length)
    		return RoleType.gt;
    	
		return RoleType.values()[index];
	}
    
    public static RoleType getRoleType(String roleType, RoleType defaValue) {
		for(RoleType rid : RoleType.values()) {
			if (rid.toString().equalsIgnoreCase(roleType))
				return rid;
		}
		
		return defaValue;
	}
    
    public static List<RoleType> getRoleTypesCanManager(String roleType) {	
		RoleType rtype = getRoleType(roleType, RoleType.gt);
		List<RoleType> roleTypes = Lists.newArrayList();
		for(RoleType rid : RoleType.values()) {
			if (rid.ordinal() < rtype.ordinal())
				roleTypes.add(rid);
		}
		
		return roleTypes;
	}
    
    public static List<RoleType> getUpwardRoleTypes(RoleType roleType) {	
		List<RoleType> roleTypes = Lists.newArrayList();
		for(RoleType rid : RoleType.values()) {
			if (rid.ordinal() > roleType.ordinal())
				roleTypes.add(rid);
		}
		
		return roleTypes;
	}
    
	
	public static RoleType getRoleType(String roleType) {	
		return getRoleType(roleType, RoleType.gt);
	}
	
	public static List<RoleType> getRoleTypes(String roleLevel) {
		List<RoleType> roleTypes = Lists.newArrayList();
		if (roleLevel.equalsIgnoreCase("s")) {
			roleTypes.add(sa);
			roleTypes.add(ad);
			roleTypes.add(ur);
			roleTypes.add(gt);
		}else if (roleLevel.equalsIgnoreCase("o")) {
			roleTypes.add(oa);
			roleTypes.add(ou);
			roleTypes.add(og);
		}else {
			roleTypes.add(ga);
			roleTypes.add(gu);
			roleTypes.add(gg);
		}
		
		return roleTypes;
	}
}
