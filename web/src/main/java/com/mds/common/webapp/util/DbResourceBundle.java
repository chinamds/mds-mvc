package com.mds.common.webapp.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import com.google.common.collect.Maps;
import com.mds.i18n.model.NeutralResource;
import com.mds.util.CacheUtils;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.PropertiesLoader;

public class DbResourceBundle extends ResourceBundle
{
    private Properties properties;

    public DbResourceBundle(Properties inProperties)
    {
        properties = inProperties;
    }

    @Override
    @SuppressWarnings("unchecked")
	public Enumeration<String> getKeys()
    {
        return properties != null ? ((Enumeration<String>) properties.propertyNames()) : null;
    }

    @Override
    protected Object handleGetObject(String key)
    {   	
        return properties.getProperty(key);
    }

    public static ResourceBundle.Control getDBControl(String sysLocale)
    {
        return new ResourceBundle.Control()
        {
            @Override
            public List<String> getFormats(String baseName)
            {
                if (baseName == null)
                {
                    throw new NullPointerException();
                }
                return Arrays.asList("db");
            }

            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException,
                  InstantiationException, IOException
            {
                if ((baseName == null) || (locale == null) || (format == null) || (loader == null))
                    throw new NullPointerException();
                ResourceBundle bundle = null;
                if (format.equals("db"))
                {
                    bundle = new DbResourceBundle(I18nUtils.getStrings(sysLocale));
                }
                return bundle;
            }

            @Override
            public long getTimeToLive(String baseName, Locale locale)
            {
                return 1000 * 60 * 30;
            }

            @Override
            public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime)
            {
                return true;
            }
            
            @Override
            public List<Locale> getCandidateLocales(String baseName, Locale locale) {
               if (baseName == null)
            	   throw new NullPointerException();
               
               if (locale.equals(new Locale("zh", "HK"))) {
		            return Arrays.asList(
		            	locale,
		            	Locale.TAIWAN,
		            	// no Locale.CHINESE here
		            	Locale.ROOT);
		       } else if (locale.equals(Locale.TAIWAN)) {
		            return Arrays.asList(
		            	locale,
		            	// no Locale.CHINESE here
		            	Locale.ROOT);
               }
               
               return super.getCandidateLocales(baseName, locale);
            }
            
           /* public Properties getStrings(String cultureCode){
            	Properties properties = I18nUtils.getStrings(cultureCode);
        		if (properties == null){
        			properties = I18nUtils.getLocalizedResources(cultureCode);
        			if (properties != null){
        				Properties applicationResources = I18nUtils.getApplicationResources();
        				if (applicationResources == null){
        					PropertiesLoader loader = new PropertiesLoader("ApplicationResources.properties");
        					applicationResources = loader.getProperties();
        			        I18nUtils.cacheApplicationResources(applicationResources);
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
        				I18nUtils.cacheLocalizedResources(cultureCode, properties);
        			}
        		}
        		
        		return properties;
        	}*/
        };
    }
}