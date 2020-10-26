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
