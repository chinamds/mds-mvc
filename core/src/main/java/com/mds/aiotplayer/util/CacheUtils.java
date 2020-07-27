/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.util;

import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.services.CachingService;
import com.mds.services.factory.MDSServicesFactory;
import com.mds.services.model.Cache;

/*import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;*/

/**
 * Cache工具类
 * @author ThinkGem
 * @version 2013-5-29
 */
public class CacheUtils {
	
	//private static CacheManager cacheManager = ((CacheManager)SpringContextHolder.getBean("ehcacheManager"));
	private static CachingService cacheManager = MDSServicesFactory.getInstance().getCachingService();

	public static final String SYS_CACHE = "sysCache";
	public static final String CM_CACHE = "cmCache";
	public static final String HRM_CACHE = "hrmCache";
	public static final String USERS_CACHE = "usersCache";
	
	public static Object get(CacheItem key) {
		return get(SYS_CACHE, key.toString());
	}

	public static Object get(String key) {
		return get(SYS_CACHE, key);
	}

	public static void put(String key, Object value) {
		put(SYS_CACHE, key, value);
	}
	
	public static void put(CacheItem key, Object value) {
		put(SYS_CACHE, key.toString(), value);
	}

	public static void remove(String key) {
		remove(SYS_CACHE, key);
	}
	
	public static void remove(CacheItem key) {
		remove(SYS_CACHE, key.toString());
	}
	
	public static Object get(String cacheName, String key) {
		/*Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();*/
		return getCache(cacheName).get(key);
	}

	public static void put(String cacheName, String key, Object value) {
		/*Element element = new Element(key, value);
		getCache(cacheName).put(element);*/
		getCache(cacheName).put(key, value);
	}

	public static void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}
	
	/**
	 * 获得一个Cache，没有则创建一个。
	 * @param cacheName
	 * @return
	 */
	public static Cache getCache(String cacheName){
		/*Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;*/
		return cacheManager.getCache(cacheName, null);
	}

	public static CachingService getCacheManager() {
		return cacheManager;
	}
	
}
