/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.service.impl;


import com.mds.sys.model.User;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-12 上午7:18
 * <p>Version: 1.0
 */
public interface PasswordManager {

    void validate(User user, String password);

    void clearLoginRecordCache(String username);
}
