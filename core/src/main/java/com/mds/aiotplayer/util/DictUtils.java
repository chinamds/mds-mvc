/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.sys.dao.DictDao;
import com.mds.aiotplayer.sys.model.Dict;
import com.mds.aiotplayer.sys.model.DictCategory;

/**
 * 字典工具类
 * @author ThinkGem
 * @version 2013-5-29
 */
public class DictUtils {
	
	private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);

	public static final String CACHE_DICT_MAP = "dictMap";
	
	public static String getDictLabel(String value, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
			for (Dict dict : getDictList(type)){
				if (type.equals(dict.getCategory().toString()) && value.equals(dict.getValue())){
					return dict.getWord();
				}
			}
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)){
			for (Dict dict : getDictList(type)){
				if (type.equals(dict.getCategory().toString()) && label.equals(dict.getWord())){
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}
	
	public static List<Dict> getDictList(String type){
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>)CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null){
			dictMap = Maps.newHashMap();
			for (Dict dict : dictDao.findAllList()){
				List<Dict> dictList = dictMap.get(dict.getCategory().toString());
				if (dictList != null){
					dictList.add(dict);
				}else{
					dictMap.put(dict.getCategory().toString(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		
		List<Dict> dictList = dictMap.get(type);
		if (dictList == null){
			dictList = Lists.newArrayList();
		}
		return dictList;
	}
	
}
