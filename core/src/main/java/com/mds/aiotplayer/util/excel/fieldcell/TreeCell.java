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
import com.mds.aiotplayer.sys.model.Area;
import com.mds.aiotplayer.sys.util.UserUtils;

/**
 * tree field/cell value convert
 * @author John Lee
 * @version 22/03/2018
 */
public class TreeCell {	
	
	/**
	 * get parent from exists or imports objects
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T getParent(List<T> imports, List<T> exists, String val, Long parentId) { 
		for (T e : imports){
			if (val.equals(((TreeEntity)e).getCode()) && ((TreeEntity)e).getParentId() == parentId){
				return e;
			}
		}
		
		for (T e : exists){
			if (val.equals(((TreeEntity)e).getCode()) && ((TreeEntity)e).getParentId() == parentId){
				return e;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T getParent(List<T> imports, List<T> exists, String val, String parentCode) { 
		for (T e : imports){
			if (val.equals(((TreeEntity)e).getCode()) && ((TreeEntity)e).getParentCode() == parentCode){
				return e;
			}
		}
		
		for (T e : exists){
			if (val.equals(((TreeEntity)e).getCode()) && ((TreeEntity)e).getParentCode() == parentCode){
				return e;
			}
		}
				
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getFullCode(Object obj, String val) {
		if (StringUtils.isBlank(val))
			return obj;
		
		((TreeEntity)obj).setParentCodes(val);
		
		return obj;
	}

	/**
	 * get value from object（export）
	 */
	@SuppressWarnings("rawtypes")
	public static String setValue(Object val) {
		if (val != null && ((TreeEntity)val).getCode() != null){
			return ((TreeEntity)val).getFullCode();
		}

		return "";
	}
}
