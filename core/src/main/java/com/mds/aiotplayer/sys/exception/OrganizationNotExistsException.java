/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.sys.exception;

import com.mds.aiotplayer.common.exception.BaseException;

/**
 * <p>Organization: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:28
 * <p>Version: 1.0
 */
public class OrganizationNotExistsException extends BaseException {

    public OrganizationNotExistsException(String info) {
        super("company.not.exists", new Object[] { info });
    }
    
    public OrganizationNotExistsException() {
        super("company.not.exists"); 
    }
}
