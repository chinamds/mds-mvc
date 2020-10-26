/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.util.excel.fieldcell;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.common.model.TreeEntity;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.util.UserUtils;

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
