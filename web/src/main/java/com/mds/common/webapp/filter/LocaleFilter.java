package com.mds.common.webapp.filter;

import com.mds.common.Constants;
import com.mds.i18n.util.I18nUtils;
import com.mds.common.webapp.util.DbResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Filter to wrap request with a request including user preferred locale.
 */
public class LocaleFilter extends OncePerRequestFilter {
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
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
                preferredLocale = (Locale) session.getAttribute(Constants.PREFERRED_LOCALE_KEY);
            } 
            
            if (preferredLocale == null){
            	preferredLocale = request.getLocale();
            }
            
            if (preferredLocale != null){
                session.setAttribute(Constants.PREFERRED_LOCALE_KEY, preferredLocale);
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
                preferredLocale = (Locale) session.getAttribute(Constants.PREFERRED_LOCALE_KEY);
            } 
            
            if (preferredLocale == null){
            	preferredLocale = request.getLocale();
            }
            
            if (preferredLocale != null){
                session.setAttribute(Constants.PREFERRED_LOCALE_KEY, preferredLocale);
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
