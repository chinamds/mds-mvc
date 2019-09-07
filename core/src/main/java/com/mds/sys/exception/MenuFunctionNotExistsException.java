/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.exception;

import com.mds.common.exception.BaseException;

/**
 * <p>MenuFunction: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:28
 * <p>Version: 1.0
 */
public class MenuFunctionNotExistsException extends BaseException {

    public MenuFunctionNotExistsException(String info) {
        super("menuFunction.not.exists", new Object[] { info });
    }
    
    public MenuFunctionNotExistsException() {
        super("menuFunction.not.exists"); 
    }
}
