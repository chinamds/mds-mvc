/**
 * Copyright &copy; 2016-2018 <a href="https://github.com/chinamds/mdsplus">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.util.excel.fieldcell;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.common.model.TreeEntity;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.util.UserUtils;

/**
 *  Menu and function field/cell convert
 * @author John Lee
 * @version 16/07/2017
 * @version 20/03/2018
 */
public class MenuFunctionCell extends TreeCell{

	/**
	 * get value from object（import）
	 */
	public static Object getValue(String val) {
		if (StringUtils.isBlank(val))
			return null;
		
		for (MenuFunction e : UserUtils.getMenuFunctions()){
			if (val.equals(e.getCode())){
				return e;
			}
		}

		return null;
	}
		
	public static Object getParentValue(List<MenuFunction> imports, String val) {
		if (StringUtils.isBlank(val))
			return UserUtils.getMenuFunctionRoot();
		
		MenuFunction parent = null;
		StringTokenizer toKenizer = new StringTokenizer(val, " > ");        
        while (toKenizer.hasMoreElements()) {         
        	//parent = getParent(importList, toKenizer.nextToken(), parent == null ? TreeEntity.getRootId() : parent.getId());
        	parent = getParent(imports, UserUtils.getMenuFunctions(), toKenizer.nextToken(), parent == null ? TreeEntity.getRootId() : parent.getId());
        }   
        		
		return parent;
	}
	
	public static MenuFunction setParent(MenuFunction menuFunction, List<MenuFunction> imports) {
		MenuFunction parent = UserUtils.getMenuFunctionRoot();
		if (StringUtils.isBlank(menuFunction.getParentCodes())) {
			menuFunction.setParent(parent);
			
			return menuFunction;
		}
		
		String[] codes = StringUtils.split(menuFunction.getParentCodes(), " > ");
		for(int i=0; i < codes.length; i++) {
			parent = getParent(imports, UserUtils.getMenuFunctions(), codes[i], parent.getCode());
		}
		menuFunction.setParent(parent);
        		
		return menuFunction;
	}
}
