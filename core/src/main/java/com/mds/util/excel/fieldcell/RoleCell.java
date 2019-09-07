/**
 * Copyright &copy; 2016-2018 <a href="https://github.com/chinamds/mdsplus">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.util.excel.fieldcell;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.mds.util.Collections3;
import com.mds.common.utils.SpringContextHolder;
import com.mds.sys.model.Role;
import com.mds.sys.service.RoleManager;

/**
 * 字段类型转换
 * @author ThinkGem
 * @version 2013-5-29
 */
public class RoleCell {

	private static RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
	
	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		List<Role> roleList = Lists.newArrayList();
		List<Role> allRoleList = roleManager.getAll();
		for (String s : StringUtils.split(val, ",")){
			for (Role e : allRoleList){
				if (e.getName().equals(s)){
					roleList.add(e);
				}
			}
		}
		return roleList.size()>0?roleList:null;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null){
			@SuppressWarnings("unchecked")
			List<Role> roleList = (List<Role>)val;
			return Collections3.extractToString(roleList, "name", ", ");
		}
		return "";
	}
	
}
