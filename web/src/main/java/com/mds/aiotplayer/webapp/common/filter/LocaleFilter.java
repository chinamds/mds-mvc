/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.filter;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.webapp.common.util.DbResourceBundle;

/**
 * Filter to wrap request with a request including user preferred locale.
 */
public class LocaleFilter implements Filter {
	/*
	 * @Autowired SessionLocaleResolver localeResolver;
	 */
	
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //noop
    }
	
	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
		String locale = request.getParameter("locale");
        Locale preferredLocale = null;

        String currLanguageTag = null; 
        //Locale sessionLocale = localeResolver.resolveLocale(httpRequest);
        Locale sessionLocale = null;
        LocaleResolver localeResolver = (LocaleResolver)SpringContextHolder.getBean(SessionLocaleResolver.class);
		if (localeResolver != null) {
			sessionLocale = localeResolver.resolveLocale(httpRequest);
		}
        if (sessionLocale != null) {
        	currLanguageTag = sessionLocale.getLanguage();
        	if (StringUtils.isNotBlank(sessionLocale.getCountry())){
        		currLanguageTag =  currLanguageTag + "_" + sessionLocale.getCountry();
        	}
        }
        
        preferredLocale = sessionLocale;
        if (StringUtils.isNotBlank(locale)) {
        	if (!locale.equals(currLanguageTag)) {
	            int indexOfUnderscore = locale.indexOf('_');
	            if (indexOfUnderscore != -1) {
	                String language = locale.substring(0, indexOfUnderscore);
	                String country = locale.substring(indexOfUnderscore + 1);
	                preferredLocale = new Locale(language, country);
	            } else {
	                preferredLocale = new Locale(locale);
	            }
        	}
        }
        
        HttpSession session = ((HttpServletRequest)request).getSession(false);
        if (preferredLocale == null) {
        	preferredLocale = request.getLocale();
        	if (preferredLocale == null)
        		preferredLocale = new Locale("en");
        }
        
        if (preferredLocale != null && !(request instanceof LocaleRequestWrapper)) {
        	httpRequest = new LocaleRequestWrapper(httpRequest, preferredLocale);
            LocaleContextHolder.setLocale(preferredLocale);
            
            localeResolver.setLocale(httpRequest, httpResponse, preferredLocale);
        }
                
    	if (session != null) {
    		javax.servlet.jsp.jstl.core.Config.set(session, Config.FMT_LOCALE, preferredLocale);
    	}
    	    	
    	String languageTag = preferredLocale.getLanguage();
    	if (StringUtils.isNotBlank(preferredLocale.getCountry())){
    		languageTag =  languageTag + "_" + preferredLocale.getCountry();
    	}
    	ResourceBundle bundle = ResourceBundle.getBundle("ApplicationResources", preferredLocale, DbResourceBundle.getDBControl(languageTag)); //"ApplicationResources_" + languageTag
        javax.servlet.jsp.jstl.core.Config.set(httpRequest, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(bundle, preferredLocale));
        
        chain.doFilter(httpRequest, response);

        // Reset thread-bound LocaleContext.
        LocaleContextHolder.setLocaleContext(null);
	}
	
	/*public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain)
          throws IOException, ServletException {*/
	/*@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
		String locale = request.getParameter("locale");
        Locale preferredLocale = null;

        if (locale != null) {
            int indexOfUnderscore = locale.indexOf('_');
            if (indexOfUnderscore != -1) {
                String language = locale.substring(0, indexOfUnderscore);
                String country = locale.substring(indexOfUnderscore + 1);
                preferredLocale = new Locale(language, country);
            } else {
                preferredLocale = new Locale(locale);
            }
        }

        HttpSession session = ((HttpServletRequest)request).getSession(false);

        if (session != null) {
            if (preferredLocale == null) {
                preferredLocale = (Locale) session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            } 
            
            if (preferredLocale == null){
            	preferredLocale = request.getLocale();
            }
            
            if (preferredLocale != null){
                session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, preferredLocale);
                //Config.set(session, Config.FMT_LOCALE, preferredLocale);
            }
            
            if (preferredLocale != null && !(request instanceof LocaleRequestWrapper)) {
                request = new LocaleRequestWrapper((HttpServletRequest)request, preferredLocale);
                LocaleContextHolder.setLocale(preferredLocale);
            }
        }else {
        	preferredLocale = request.getLocale();
        }
        
        if (preferredLocale != null){
        	if (session != null) {
        		javax.servlet.jsp.jstl.core.Config.set( session, Config.FMT_LOCALE, preferredLocale);
        	}
        	String languageTag = preferredLocale.getLanguage();
        	if (StringUtils.isNotBlank(preferredLocale.getCountry())){
        		languageTag =  languageTag + "_" + preferredLocale.getCountry();
        	}
        	ResourceBundle bundle = ResourceBundle.getBundle("ApplicationResources", preferredLocale, DbResourceBundle.getDBControl(languageTag)); //"ApplicationResources_" + languageTag
            javax.servlet.jsp.jstl.core.Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(bundle, preferredLocale));
        }else{
        	preferredLocale = new Locale("en");
        	if (session != null) {
        		javax.servlet.jsp.jstl.core.Config.set( session, Config.FMT_LOCALE, preferredLocale);
        	}
        	ResourceBundle bundle = ResourceBundle.getBundle("ApplicationResources", preferredLocale, DbResourceBundle.getDBControl("en")); //"ApplicationResources_" + languageTag
            javax.servlet.jsp.jstl.core.Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(bundle, preferredLocale));
        }
        
        chain.doFilter(request, response);

        // Reset thread-bound LocaleContext.
        LocaleContextHolder.setLocaleContext(null);
	}*/
	
	@Override
    public void destroy() {
        //noop
    }

    /**
     * This method looks for a "locale" request parameter. If it finds one, it sets it as the preferred locale
     * and also configures it to work with JSTL.
     *
     * @param request the current request
     * @param response the current response
     * @param chain the chain
     * @throws IOException when something goes wrong
     * @throws ServletException when a communication failure happens
     */
    /*public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain chain)
            throws IOException, ServletException {

        String locale = request.getParameter("locale");
        Locale preferredLocale = null;

        if (locale != null) {
            int indexOfUnderscore = locale.indexOf('_');
            if (indexOfUnderscore != -1) {
                String language = locale.substring(0, indexOfUnderscore);
                String country = locale.substring(indexOfUnderscore + 1);
                preferredLocale = new Locale(language, country);
            } else {
                preferredLocale = new Locale(locale);
            }
        }

        HttpSession session = request.getSession(false);

        if (session != null) {
            if (preferredLocale == null) {
                preferredLocale = (Locale) session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
            } 
            
            if (preferredLocale == null){
            	preferredLocale = request.getLocale();
            }
            
            if (preferredLocale != null){
                session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, preferredLocale);
                Config.set(session, Config.FMT_LOCALE, preferredLocale);
            }
            
            if (preferredLocale != null && !(request instanceof LocaleRequestWrapper)) {
                request = new LocaleRequestWrapper(request, preferredLocale);
                LocaleContextHolder.setLocale(preferredLocale);
            }
        }else {
        	preferredLocale = request.getLocale();
        }
        
        if (preferredLocale != null){
        	String languageTag = preferredLocale.getLanguage();
        	if (StringUtils.isNotBlank(preferredLocale.getCountry())){
        		languageTag =  languageTag + "_" + preferredLocale.getCountry();
        	}
        	ResourceBundle bundle = ResourceBundle.getBundle("ApplicationResources", preferredLocale, DbResourceBundle.getDBControl(languageTag)); //"ApplicationResources_" + languageTag
            javax.servlet.jsp.jstl.core.Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(bundle, preferredLocale));
        }else{
        	preferredLocale = new Locale("en");
        	ResourceBundle bundle = ResourceBundle.getBundle("ApplicationResources", preferredLocale, DbResourceBundle.getDBControl("en")); //"ApplicationResources_" + languageTag
            javax.servlet.jsp.jstl.core.Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(bundle, preferredLocale));
        }
        
        chain.doFilter(request, response);

        // Reset thread-bound LocaleContext.
        LocaleContextHolder.setLocaleContext(null);
    }*/
}
