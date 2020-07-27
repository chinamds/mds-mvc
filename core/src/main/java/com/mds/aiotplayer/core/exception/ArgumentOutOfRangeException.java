/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.core.exception;

/**
 * <p>EPerson: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
public class ArgumentOutOfRangeException extends ArgumentException {

	//
    // Summary:
    //     Initializes a new instance of the System.ArgumentOutOfRangeException class with a specified
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
    public ArgumentOutOfRangeException(String paramName, Throwable cause) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName, cause);
    	this(paramName, null, cause);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.ArgumentOutOfRangeException class with a specified
    //     error message and the name of the parameter that causes this exception.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    //
    //   paramName:
    //     The name of the parameter that caused the current exception.
    public ArgumentOutOfRangeException(String paramName) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, null, null);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.ArgumentOutOfRangeException class with a specified
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
    public ArgumentOutOfRangeException(String paramName, String module, Throwable cause) {
    	super(module, paramName, null, cause);    	
    }
    
    public ArgumentOutOfRangeException(String paramName, String module) {
        //super(module, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, module, null);
    }
}