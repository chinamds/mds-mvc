/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.service.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mds.sys.exception.UserPasswordNotMatchException;
import com.mds.sys.exception.UserPasswordRetryLimitExceedException;
import com.mds.sys.model.User;
import com.mds.util.HelperFunctions;

import javax.annotation.PostConstruct;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-12 上午7:18
 * <p>Version: 1.0
 */
@Service
public class PasswordManagerImpl  implements PasswordManager{

    @Autowired
    private CacheManager ehcacheManager;

    private Cache loginRecordCache;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount = 10;

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @PostConstruct
    public void init() {
        loginRecordCache = ehcacheManager.getCache("loginRecordCache");
    }

    public void validate(User user, String password) {
        String username = user.getUsername();

        int retryCount = 0;

        Element cacheElement = loginRecordCache.get(username);
        if (cacheElement != null) {
            retryCount = (Integer) cacheElement.getObjectValue();
            if (retryCount >= maxRetryCount) {
                /*UserLogUtils.log(
                        username,
                        "passwordError",
                        "password error, retry limit exceed! password: {},max retry count {}",
                        password, maxRetryCount);*/
                throw new UserPasswordRetryLimitExceedException(maxRetryCount);
            }
        }

        if (!HelperFunctions.validatePassword(password, user.getPassword())) {
            loginRecordCache.put(new Element(username, ++retryCount));
            /*UserLogUtils.log(
                    username,
                    "passwordError",
                    "password error! password: {} retry count: {}",
                    password, retryCount);*/
            throw new UserPasswordNotMatchException();
        } else {
            clearLoginRecordCache(username);
        }
    }

    public void clearLoginRecordCache(String username) {
        loginRecordCache.remove(username);
    }
}
