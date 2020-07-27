/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

//import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.mds.aiotplayer.common.Constants;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-9 下午8:49
 * <p>Version: 1.0
 */
public class MessageUtils {

    //private static MessageSource messageSource;

    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public static String message(String code, Object... args) {
    	 Locale locale = LocaleContextHolder.getLocale();
         
    	 return message(code, locale, args);
         
        /*if (messageSource == null) {
            messageSource = SpringContextHolder.getBean(MessageSource.class);
        }

        return messageSource.getMessage(code, args, locale);*/
    }

    public static String message(String code, Locale locale, Object... args) {
        try {
            return ResourceBundle.getBundle(Constants.BUNDLE_KEY, locale)
                    .getString(code);
        } catch (MissingResourceException mse) {
            return code;
        }
   }
    
    @SuppressWarnings("unchecked")
    public static void saveError(HttpServletRequest request, String error) {
        List errors = (List) request.getSession().getAttribute(Constants.ERRORS_MESSAGES_KEY);
        if (errors == null) {
            errors = new ArrayList();
        }
        errors.add(error);
        request.getSession().setAttribute(Constants.ERRORS_MESSAGES_KEY, errors);
    }
    
    @SuppressWarnings("unchecked")
    public static void saveMessage(HttpServletRequest request, String msg) {
        List messages = (List) request.getSession().getAttribute(Constants.MESSAGES_KEY);

        if (messages == null) {
            messages = new ArrayList();
        }

        messages.add(msg);
        request.getSession().setAttribute(Constants.MESSAGES_KEY, messages);
    }
}
