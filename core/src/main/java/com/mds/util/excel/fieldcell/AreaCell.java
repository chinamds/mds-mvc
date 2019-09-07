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
import com.mds.sys.model.Area;
import com.mds.sys.util.UserUtils;

/**
 *  Area field/cell convert
 * @author John Lee
 * @version 20/03/2018
 */
public class AreaCell extends TreeCell{

	/**
	 * get object for import
	 */
	public static Object getValue(String val) {
		if (StringUtils.isBlank(val))
			return null;
		
		for (Area e : UserUtils.getAreaList()){
			if (val.equals(e.getCode())){
				return e;
			}
		}
		return null;
	}

	public static Object getParentValue(List<Area> imports, String val) {
		if (StringUtils.isBlank(val))
			return UserUtils.getAreaRoot();
		
		Area parent = null;
		StringTokenizer toKenizer = new StringTokenizer(val, " > ");        
        while (toKenizer.hasMoreElements()) {         
        	//parent = getParent(importList, toKenizer.nextToken(), parent == null ? TreeEntity.getRootId() : parent.getId());
        	parent = getParent(imports, UserUtils.getAreaList(), toKenizer.nextToken(), parent == null ? TreeEntity.getRootId() : parent.getId());
        }   
        		
		return parent;
	}
	
	public static Area setParent(Area area, List<Area> imports) {
		Area parent = UserUtils.getAreaRoot();
		if (StringUtils.isBlank(area.getParentCodes())) {
			area.setParent(parent);
			
			return area;
		}
		
		String[] codes = StringUtils.split(area.getParentCodes(), " > ");
		for(int i=0; i < codes.length; i++) {
			parent = getParent(imports, UserUtils.getAreaList(), codes[i], parent.getCode());
		}
		area.setParent(parent);
        		
		return area;
	}
}
