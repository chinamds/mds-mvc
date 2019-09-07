/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.exception;

import com.mds.common.exception.BaseException;

/**
 * <p>Area: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:28
 * <p>Version: 1.0
 */
public class AreaNotExistsException extends BaseException {

    public AreaNotExistsException(String info) {
        super("area.not.exists", new Object[] { info });
    }
    
    public AreaNotExistsException() {
        super("area.not.exists"); 
    }
}
