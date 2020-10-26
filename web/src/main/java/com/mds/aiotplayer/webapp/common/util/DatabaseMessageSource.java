/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

import java.text.MessageFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.mds.aiotplayer.i18n.util.I18nUtils;

public class DatabaseMessageSource extends AbstractMessageSource implements ResourceLoaderAware {
	protected static final Logger log = LoggerFactory.getLogger(DatabaseMessageSource.class);
	
	private ResourceLoader resourceLoader;
	
	public DatabaseMessageSource() {
    }
	
    protected MessageFormat resolveCode(String code, Locale locale) {
        return createMessageFormat(getText(code, locale), locale);
    }

    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return getText(code, locale);
    }
    
    private String getText(String code, Locale locale) {
    	String msg = I18nUtils.getDBString(code, locale);
        if (msg == null) {
        	log.debug("DatabaseMessageSource - getDBString Key: {} not found, Locale: {}", code, locale.getDisplayName());
            try {
            	msg = getParentMessageSource().getMessage(code, null, locale);
            } catch (Exception e) {
            	msg = null;
            }
            if (msg == null)
            	msg = super.resolveCodeWithoutArguments(code, locale);
        }
            	
    	return msg != null ? msg : code;
    }
    
	@Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
    }
}