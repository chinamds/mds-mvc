/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.exception;

import com.mds.common.exception.BaseException;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
public class UserException extends BaseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3761364955574193301L;

	public UserException(String code, Object[] args) {
        super("user", code, args, code);
    }

}
