package com.mds.common.webapp.util;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.mds.i18n.util.I18nUtils;

public class DatabaseMessageSource extends AbstractMessageSource implements ResourceLoaderAware {
	
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