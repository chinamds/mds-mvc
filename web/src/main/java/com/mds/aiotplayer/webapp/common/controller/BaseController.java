/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simple class to retrieve and send a password hint to users.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class BaseController {
    private final Logger log = LoggerFactory.getLogger(BaseController.class);

    @SuppressWarnings("unchecked")
    public void saveError(HttpServletRequest request, String error) {
        List errors = (List) request.getSession().getAttribute(BaseFormController.ERRORS_MESSAGES_KEY);
        if (errors == null) {
            errors = new ArrayList();
        }
        errors.add(error);
        request.getSession().setAttribute(BaseFormController.ERRORS_MESSAGES_KEY, errors);
    }

    // this method is also in BaseForm Controller
    @SuppressWarnings("unchecked")
    public void saveMessage(HttpServletRequest request, String msg) {
        List messages = (List) request.getSession().getAttribute(BaseFormController.MESSAGES_KEY);
        if (messages == null) {
            messages = new ArrayList();
        }
        messages.add(msg);
        request.getSession().setAttribute(BaseFormController.MESSAGES_KEY, messages);
    }
    
    //Error handling.
    @SuppressWarnings("unchecked")
	public void saveError(HttpServletRequest request, BindingResult bindingResult) {
    	List errors = (List) request.getSession().getAttribute(BaseFormController.ERRORS_MESSAGES_KEY);
        if (errors == null) {
            errors = new ArrayList();
        }
        
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        String message = objectError.getDefaultMessage();
        errors.add(message);
        
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            String fieldName = fieldError.getField();
            errors.add(fieldName + " " + message);
            String[] codes = fieldError.getCodes();
            if (codes != null && codes.length > 2) {
                if ("typeMismatch.java.util.Date".equals(codes[2])) {
                    errors.add(fieldName + " is Invalid date format");
                }
            }
        }
    }
    
    /**
     * Convenience method for getting a i18n key's value.  Calling
     * getMessageSourceAccessor() is used because the RequestContext variable
     * is not set in unit tests b/c there's no DispatchServlet Request.
     *
     * @param msgKey
     * @param locale the current locale
     * @return
     */
    public String getText(String msgKey, Locale locale) {
        //return messages.getMessage(msgKey, locale);
    	return I18nUtils.getString(msgKey, locale);
    }

    /**
     * Convenient method for getting a i18n key's value with a single
     * string argument.
     *
     * @param msgKey
     * @param arg
     * @param locale the current locale
     * @return
     */
    public String getText(String msgKey, String arg, Locale locale) {
        //return getText(msgKey, new Object[] { arg }, locale);
    	return I18nUtils.getString(msgKey, locale, arg);
    }

    /**
     * Convenience method for getting a i18n key's value with arguments.
     *
     * @param msgKey
     * @param args
     * @param locale the current locale
     * @return
     */
    public String getText(String msgKey, Object[] args, Locale locale) {
        //return messages.getMessage(msgKey, args, locale);
    	return I18nUtils.getString(msgKey, locale, args);
    }
}
