/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.common.exception;

import com.mds.common.exception.BaseException;

/**
 * <p>EPerson: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
public class EncryptDecryptException extends BaseException {

	//
    // Summary:
    //     Initializes a new instance of the EncryptDecryptException class with a specified
    //     error message.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    public EncryptDecryptException(String message) {
    	super(message);
    }
    
    //
    // Summary:
    //     Initializes a new instance of the EncryptDecryptException class with a specified
    //     error message and a reference to the inner exception that is the cause of this
    //     exception.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    //
    //   cause:
    //     The exception that is the cause of the current exception. If the cause
    //     parameter is not a null reference, the current exception is raised in a catch
    //     block that handles the inner exception.
    public EncryptDecryptException(String paramName, Object paramValue, Throwable cause) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName, cause);
    	this(paramName, paramValue, null, cause);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.EncryptDecryptException class with a specified
    //     error message and the name of the parameter that causes this exception.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    //
    //   paramName:
    //     The name of the parameter that caused the current exception.
    public EncryptDecryptException(String paramName, Object paramValue) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, paramValue, null, null);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.EncryptDecryptException class with a specified
    //     error message, the parameter name, and a reference to the inner exception that
    //     is the cause of this exception.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    //
    //   paramName:
    //     The name of the parameter that caused the current exception.
    //
    //   cause:
    //     The exception that is the cause of the current exception. If the cause
    //     parameter is not a null reference, the current exception is raised in a catch
    //     block that handles the inner exception.
    public EncryptDecryptException(String paramName, Object paramValue, String module, Throwable cause) {
    	super(module, paramValue == null ? "exception.argument.null" : "exception.argument"
    		, new Object[] {paramName, paramValue}, "Invalid argument: " + paramName, cause);    	
    }
    
    public EncryptDecryptException(String paramName, Object paramValue, String module) {
        //super(module, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, paramValue, module, null);
    }
}
