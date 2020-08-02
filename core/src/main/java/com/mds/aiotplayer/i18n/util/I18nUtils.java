/**
 * Copyright &copy; 2012-2013 <a href="/mdsplus">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.i18n.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.PropertiesLoader;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.UserAction;
import com.mds.aiotplayer.core.WorkflowType;
import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.i18n.service.LocalizedResourceManager;
import com.mds.aiotplayer.i18n.service.NeutralResourceManager;
import com.mds.aiotplayer.i18n.service.impl.CultureManagerImpl;
import com.mds.aiotplayer.i18n.service.impl.NeutralResourceManagerImpl;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.i18n.model.Culture;
import com.mds.aiotplayer.i18n.model.LocalizedResource;
import com.mds.aiotplayer.i18n.model.NeutralResource;

/**
 * i18n resource manager
 * @author John Lee
 * @version 2017-04-19
 */
public class I18nUtils {
	/**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected static final Logger log = LoggerFactory.getLogger(I18nUtils.class);
    
	//private static CultureManager cultureManager = (CultureManager)SpringContextHolder.getBean(CultureManager.class);
	//private static NeutralResourceManager neutralResourceManager = (NeutralResourceManager)SpringContextHolder.getBean(NeutralResourceManager.class);
	//private static LocalizedResourceManager localizedResourceManager = SpringContextHolder.getBean(LocalizedResourceManager.class);

	public static final String CACHE_I18N_MAP = "i18nMap";
	public static final String NEUTRAL_KEY = "NeutralResource";
	public static final String APPLICATIONRESOURCES_KEY = "ApplicationResources";
	
	private static final String[] JsResource = {"menuFunction.myprofile", "menuFunction.mymessage", "menuFunction.viewallnotifications", 
    		"menuFunction.mynotifications", "button.ok", "button.close", "myCalendar.addevent", "myCalendar.viewevent", 
    		"myCalendar.deleteevent", "myCalendar.suretodelete", "button.import", "button.ok", "button.yes", 
    		"button.no", "button.cancel", "errors.system.title", "errors.system.msg", "errors.request.title", 
    		"errors.request.msg", "button.save", "entity.saved", "entity.deleted", "button.delete", "button.selectall", 
    		"button.invertselection", "table.message.norecordselected", "table.message.deleteconfirm"};
	
	public static Culture getCulture(final String cultureCode){
		for (Culture culture : getCultures()){
			if (cultureCode.equals(culture.getCultureCode())){
				return culture;
			}
		}
				
		return null;
	}
	
	public static List<Culture> getCultures(){
		/*@SuppressWarnings("unchecked")
		List<Culture> cultures = (List<Culture>)CacheUtils.get(CACHE_CULTURE_LIST);
		if (cultures == null){
			cultures = cultureManager.getAll();
			CacheUtils.put(CACHE_CULTURE_LIST, cultures);
		}
		
		return cultures;*/
		CultureManager cultureManager = (CultureManager)SpringContextHolder.getBean(CultureManager.class);
		
		return cultureManager.getCultures();
	}
	
	public static Culture getCulture(Locale locale){
		String languageTag = "en";
		if (locale != null) {
			languageTag = locale.getLanguage();
	    	if (StringUtils.isNotBlank(locale.getCountry())){
	    		languageTag =  languageTag + "_" + locale.getCountry();
	    	}
		}
		
    	List<Culture> cultures  = getCultures();
    	for(Culture culture : cultures) {
    		if (culture.getCultureCode().equals(languageTag)) {
    			return culture;
    		}
    	}
    	
    	for(Culture culture : cultures) {
    		if (culture.getCultureCode().equals("en_US")) {
    			return culture;
    		}
    	}
    	
    	for(Culture culture : cultures) {
    		if (culture.getCultureCode().equals("en")) {
    			return culture;
    		}
    	}
    	
		return null;
	}
	
	public static String getCultureName(Locale locale){
		//locale = request.getLocale();
		Culture culture = getCulture(locale);
		if (culture != null)
			return culture.getCultureName();
    	
		return StringUtils.EMPTY;
	}
	
	public static String getLanguageTag(HttpServletRequest request){
		Locale locale = null;
		
		LocaleResolver localeResolver = (LocaleResolver)SpringContextHolder.getBean(SessionLocaleResolver.class);
		if (localeResolver != null) {
			locale = localeResolver.resolveLocale(request);
		}
		if (locale == null) {
			locale = request.getLocale();
		}
			
		
		/*HttpSession session = request.getSession(false);

  	    Locale locale = null;
        if (session != null) {
          	locale = (Locale) session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        }else {
       	  locale = request.getLocale();
        }*/

        String languageTag = "en";
        if (locale != null) {
	        languageTag = locale.getLanguage();
		   	if (StringUtils.isNotBlank(locale.getCountry())){
		   		languageTag =  languageTag + "_" + locale.getCountry();
		   	}
        }
   	
		return languageTag;
	}
	
	public static String getLanguageName(HttpServletRequest request){
		Locale locale = null;
		
		LocaleResolver localeResolver = (LocaleResolver)SpringContextHolder.getBean(SessionLocaleResolver.class);
		if (localeResolver != null) {
			locale = localeResolver.resolveLocale(request);
		}
		if (locale == null) {
			locale = request.getLocale();
		}
		 /*HttpSession session = request.getSession(false);

   	    Locale locale = null;
        if (session != null) {
           	locale = (Locale) session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        }else {
        	locale = request.getLocale();
        }*/

        Culture culture = getCulture(locale);
		if (culture != null)
			return culture.getCultureName();
    	
		return StringUtils.EMPTY;
	}
	
	public static HashMap<String, Object> getStrings(String[] msgKeys, Locale locale, Object...value){
		String languageTag = locale.getLanguage();
    	if (StringUtils.isNotBlank(locale.getCountry())){
    		languageTag =  languageTag + "_" + locale.getCountry();
    	}
    	if (msgKeys == null)
    		msgKeys = JsResource;
    	
    	HashMap<String,Object> resultMap = new LinkedHashMap<String, Object>();
    	for(String msgKey : msgKeys){
    		resultMap.put(msgKey.replace(".", "_"), getString(msgKey, languageTag, value)); //
    	}
    			
		return resultMap;
	}
	
	public static String getString(String msgKey, Locale locale, Object...value){
		String languageTag = locale.getLanguage();
    	if (StringUtils.isNotBlank(locale.getCountry())){
    		languageTag =  languageTag + "_" + locale.getCountry();
    	}
    	
    	log.debug("getString Key: {}, Locale: {}", msgKey, languageTag);
    			
		return getString(msgKey, languageTag, value);
	}
	
	public static String getString(String msgKey, String cultureCode, Object...value){
		String msgValue = getStringFallback(msgKey, cultureCode);
		if(null != value && value.length != 0  && msgValue != null){			
			return StringUtils.format(msgValue, value);
		}
		if (msgValue != null){
			if(null != value && value.length != 0){			
				return StringUtils.format(msgValue, value);
			}
			
			return msgValue;
		}
		
		return msgKey;
	}
	
	//get string default locale
	public static String getMessage(String msgKey){
		Locale locale = LocaleContextHolder.getLocale();
		
		String languageTag = locale.getLanguage();
    	if (StringUtils.isNotBlank(locale.getCountry())){
    		languageTag =  languageTag + "_" + locale.getCountry();
    	}
    		
    	
    	log.debug("getMessage Key: {}, Locale: {}", msgKey, languageTag);
		return getString(msgKey, languageTag);
	}
	
	//get string default locale with parameter
	public static String getMessage(String msgKey, Object...value){
		Locale locale = LocaleContextHolder.getLocale();
		return getString(msgKey, locale, value);
	}	
	
	public static String getString(String msgKey, Locale locale){
		if (locale == null)
			locale = LocaleContextHolder.getLocale();
		
		String languageTag = locale.getLanguage();
    	if (StringUtils.isNotBlank(locale.getCountry())){
    		languageTag =  languageTag + "_" + locale.getCountry();
    	}
    	
    	log.debug("getString Key: {}, Locale: {}", msgKey, languageTag);
    			
		return getString(msgKey, languageTag);
	}
	
	public static String getString(String msgKey, HttpServletRequest request){		
		String languageTag = getLanguageTag(request);
		
		log.debug("getString Key: {}, Locale: {}", msgKey, languageTag);
    			
		return getString(msgKey, languageTag);
	}
	
	public static String getString(String msgKey, String cultureCode){  	
		String msgValue = getStringFallback(msgKey, cultureCode);
		if (msgValue != null)
			return msgValue;
		
		return msgKey;
	}
	
	private static String getStringFallback(String msgKey, String cultureCode){
		Properties properties = getStrings(cultureCode);
		String msgValue = null;
		if (properties != null){
			msgValue = properties.getProperty(msgKey);
		}
		
		if (msgValue == null){
			properties = getStrings(NEUTRAL_KEY);
			if (properties != null){
				msgValue = properties.getProperty(msgKey);
			}
		}
		
		if (msgValue == null){
			properties = getApplicationResources();
			if (properties != null){
				msgValue = properties.getProperty(msgKey);
			}
		}
			
		return msgValue;
	}
	
	public static String getLanguageTag(Locale locale){
		String languageTag = locale.getLanguage();
    	if (StringUtils.isNotBlank(locale.getCountry())){
    		languageTag =  languageTag + "_" + locale.getCountry();
    	}
    	
    	return languageTag;
	}
	
	public static String getCurrentLanguageTag() {   	
        return getLanguageTag(LocaleContextHolder.getLocale());
    }
	
	
	/* use in DatabseMessageSource*/
	public static String getDBString(String msgKey, Locale locale){
		String languageTag = locale.getLanguage();
    	if (StringUtils.isNotBlank(locale.getCountry())){
    		languageTag =  languageTag + "_" + locale.getCountry();
    	}
    	
    	log.debug("getDBString Key: {}, Locale: {}", msgKey, languageTag);
    	
		Properties properties = getStrings(languageTag);		
		if (properties != null){
			String msgValue = properties.getProperty(msgKey);
			if (msgValue == null){
				properties = getStrings(NEUTRAL_KEY);
				if (properties != null){
					msgValue = properties.getProperty(msgKey);
				}
			}
			return msgValue;
		}
		
		return null;
	}
	
	public static Properties getStrings(String cultureCode){
		@SuppressWarnings("unchecked")
		Map<String, Properties> resourceMap = (Map<String, Properties>)CacheUtils.get(CACHE_I18N_MAP);
		if (resourceMap == null){		
			resourceMap = cacheNeutralResources();
		}
		
		Properties properties = resourceMap.get(cultureCode);
		if (properties == null){
			properties = getLocalizedResources(cultureCode);
			if (properties == null){
				properties = resourceMap.get(NEUTRAL_KEY);
			}
		}
		
		return properties;
		//return resourceMap.get(cultureCode);
	}
	
	public static synchronized Map<String, Properties> cacheNeutralResources(){
		@SuppressWarnings("unchecked")
		Map<String, Properties> resourceMap = (Map<String, Properties>)CacheUtils.get(CACHE_I18N_MAP);
		if (resourceMap == null)
			resourceMap = Maps.newHashMap();
	
		Properties properties = new Properties();
		for (NeutralResource neutralResource : getNeutralResources()){
			/*properties.setProperty(neutralResource.getResourceClass() + "." + neutralResource.getResourceKey()
				, neutralResource.getValue());*/
			properties.setProperty(neutralResource.getResourceKey()
					, neutralResource.getValue());
		}
		resourceMap.put(NEUTRAL_KEY, properties);
		CacheUtils.put(CACHE_I18N_MAP, resourceMap);	
		
		return resourceMap;
	}
	
	public static Properties getLocalizedResources(final String cultureCode){
		Culture culture = getCulture(cultureCode);
		Properties properties = null;
		if (culture != null)
		{
			properties = new Properties();
			log.debug("i18nUtils-getLocalizedResources get localized resources '{}' from db", cultureCode);
			LocalizedResourceManager localizedResourceManager = SpringContextHolder.getBean(LocalizedResourceManager.class);
			List<LocalizedResource> localizedResources = localizedResourceManager.findByCultureId(culture.getId());
			//log.debug("i18nUtils-getLocalizedResources get neutral Resource Mappings from db");
			//List<Map<Long, Long>> neutralResourceMappings = localizedResourceManager.findNeutralMap(culture.getId());
			log.debug("i18nUtils-getLocalizedResources init Properties object, localized resources '{}', get Database Records: '{}'", cultureCode, localizedResources.size());
			for (NeutralResource neutralResource : getNeutralResources()){
				String label = neutralResource.getValue();
				Optional<LocalizedResource> optLocalizedResource = localizedResources.stream().filter(l->l.getNeutralResource().getId().equals(neutralResource.getId())).findFirst();
				if (optLocalizedResource.isPresent()) {
					label = optLocalizedResource.get().getValue();
					
					//log.debug("i18nUtils-getLocalizedResources '{}' init Key: '{}', Value: '{}'", cultureCode, neutralResource.getResourceKey(), label);					
				}
				/*Optional<Map<Long, Long>> optMap = neutralResourceMappings.stream().filter(m->m.containsKey(neutralResource.getId())).findFirst();
				if (optMap.isPresent()) {
					Optional<LocalizedResource> optLocalizedResource = localizedResources.stream().filter(l->l.getId() == optMap.get().get(neutralResource.getId())).findFirst();
					if (optLocalizedResource.isPresent())
						label = optLocalizedResource.get().getValue();
				}*/
				/*for (LocalizedResource localizedResource : localizedResources){
					if (localizedResource.getNeutralResource().getId() == neutralResource.getId()){
						label = localizedResource.getValue();
						break;
					}
				}*/
			
				//properties.setProperty(neutralResource.getResourceClass() + "." + neutralResource.getResourceKey(), label);
				properties.setProperty(neutralResource.getResourceKey(), label);
			}
		}
		
		if (properties != null){
			Properties applicationResources = getApplicationResources();
			if (applicationResources == null){
				PropertiesLoader loader = new PropertiesLoader("ApplicationResources.properties");
				applicationResources = loader.getProperties();
		        cacheApplicationResources(applicationResources);
			}
			if (applicationResources != null)
			{
					// walk values, interpolating any embedded references.
		        for (Enumeration<?> pe = applicationResources.propertyNames(); pe.hasMoreElements(); )
		        {
		            String key = (String)pe.nextElement();
		            if (!properties.containsKey(key))
		            {
		                properties.setProperty(key, applicationResources.getProperty(key));
		            }
		        }
			}
			cacheLocalizedResources(cultureCode, properties);
		}
			
		return properties;
	}
	
	public static synchronized void cacheLocalizedResources(String cultureCode, Properties localizedResources){
		@SuppressWarnings("unchecked")
		Map<String, Properties> resourceMap = (Map<String, Properties>)CacheUtils.get(CACHE_I18N_MAP);
		if (resourceMap == null)
			resourceMap = Maps.newHashMap();
			
		resourceMap.put(cultureCode, localizedResources);
		CacheUtils.put(CACHE_I18N_MAP, resourceMap);
	}
		
	public static NeutralResource getNeutralResource(final String resourceKey){
		NeutralResourceManager neutralResourceManager = (NeutralResourceManager)SpringContextHolder.getBean(NeutralResourceManager.class);
		Optional<NeutralResource> optNeutralResources= neutralResourceManager.getNeutralResources().stream().filter(n->n.getResourceKey() == resourceKey).findFirst();
		return optNeutralResources.isPresent() ? optNeutralResources.get() : null;
	}
	
	public static List<NeutralResource> getNeutralResources(){
		NeutralResourceManager neutralResourceManager = (NeutralResourceManager)SpringContextHolder.getBean(NeutralResourceManager.class);
		/*@SuppressWarnings("unchecked")
		List<NeutralResource> neutralResources = (List<NeutralResource>)CacheUtils.get(CACHE_I18N_NEUTRAL);
		if (neutralResources == null){
			neutralResources = neutralResourceManager.getNeutralResources();
			CacheUtils.put(CACHE_I18N_NEUTRAL, neutralResources);
		}
		
		return neutralResources;*/
		return neutralResourceManager.getNeutralResources();
	}
	
	public static void cacheApplicationResources(Properties applicationResources){
		CacheUtils.put(APPLICATIONRESOURCES_KEY, applicationResources);
	}
	
	public static Properties getApplicationResources(){
		return (Properties)CacheUtils.get(APPLICATIONRESOURCES_KEY);
	}
	
	/// <summary>
	/// Remove i18n items from cache. This includes neutral Resources, localized Resources, and more.
	/// </summary>
	public static void purgeCache(){
		CacheUtils.remove(CACHE_I18N_MAP);
	}
	
	public static String UserActionConverter(List<UserAction> userActions, Locale locale) {
		String actionStr = "";
		if (userActions != null && !userActions.isEmpty()) {
			for(UserAction action : userActions) {
				String str = getStringFallback(action.getLabel(), getLanguageTag(locale));
				if (StringUtils.isBlank(str)) {
					str = action.toString();
				}
	    		if (StringUtils.isBlank(actionStr)) {
	    			actionStr =  str;
	    		}else {
	    			actionStr += ",";
	    			actionStr +=  str;
	    		}
	    	}
		}
		
		return actionStr;
	}
	
	public static String getString(Class cls, String sub, HttpServletRequest request) {
		String key = StringUtils.lowerFirst(cls.getSimpleName()) + "." + StringUtils.lowerFirst(sub);
		String title =  I18nUtils.getString(key, request.getLocale());
		if (StringUtils.isBlank(title) || title.equals(key) )
			title = sub;
		
		return title;
	}
	
	public static String getRoleType(RoleType roleType, HttpServletRequest request) {
		String key = StringUtils.lowerFirst(RoleType.class.getSimpleName()) + "." + StringUtils.lowerFirst(roleType.toString());
		String title =  I18nUtils.getString(key, request.getLocale());
		if (StringUtils.isBlank(title) || title.equals(key) )
			title = roleType.getInfo();
		
		return title;
	}
	
	public static String getResourceId(ResourceId resourceId, HttpServletRequest request) {
		String title =  I18nUtils.getString("menu." + resourceId.toString().replace('_', '.') + Constants.Suffix_Title, request.getLocale());
	
		return title;
	}
	
	public static String getWorkflowType(WorkflowType workflowType, HttpServletRequest request) {
		String key = StringUtils.lowerFirst(WorkflowType.class.getSimpleName()) + "."  + StringUtils.lowerFirst(workflowType.toString());
		String title =  I18nUtils.getString(key, request.getLocale());
	
		return title;
	}
}
