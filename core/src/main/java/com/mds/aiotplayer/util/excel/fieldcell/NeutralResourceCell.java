/**
 * Copyright &copy; 2016-2018 <a href="https://github.com/chinamds/mdsplus">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.util.excel.fieldcell;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.util.Collections3;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.i18n.model.NeutralResource;

/**
 * Neutral Resource Cell
 * @author John Lee
 * @version 16/07/2017
 */
public class NeutralResourceCell {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		if (StringUtils.isNotBlank(val)) {
			List<NeutralResource> neutralResources = I18nUtils.getNeutralResources();
			for (NeutralResource e : neutralResources){
				if (e.getResourceKey().equals(val)){
					return e;
				}
			}
		}
		
		return null;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((NeutralResource)val).getResourceKey() != null){
			return ((NeutralResource)val).getResourceKey();
		}
		return "";
	}
	
}
