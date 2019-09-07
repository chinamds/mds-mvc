/**
 * Copyright &copy; 2016-2018 <a href="https://github.com/chinamds/mdsplus">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.util.excel.fieldcell;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.mds.common.model.TreeEntity;
import com.mds.sys.model.Organization;
import com.mds.sys.util.UserUtils;

/**
 * field type convert
 * @author John Lee
 * @version 16/07/2017
 */
public class OrganizationCell extends TreeCell {

	/**
	 * get value from object（import）
	 */
	public static Object getValue(String val) {
		if (StringUtils.isBlank(val))
			return null;
		
		for (Organization e : UserUtils.getOrganizationList()){
			if (val.equals(e.getCode())){
				return e;
			}
		}

		return null;
	}
	
	public static Object getParentValue(List<Organization> importList, String val) {
		if (StringUtils.isBlank(val))
			return null;
		
		Organization parent = null;
		StringTokenizer toKenizer = new StringTokenizer(val, " > ");        
        while (toKenizer.hasMoreElements()) {         
        	//parent = getParent(importList, toKenizer.nextToken(), parent == null ? TreeEntity.getRootId() : parent.getId());
        	parent = getParent(importList, UserUtils.getOrganizationList(), toKenizer.nextToken(), parent == null ? TreeEntity.getRootId() : parent.getId());
        }   
        		
		return parent;
	}
}
