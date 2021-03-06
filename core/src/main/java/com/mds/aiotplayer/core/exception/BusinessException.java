/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core.exception;

import com.mds.aiotplayer.common.exception.BaseException;

/**
 * <p>EPerson: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
public class BusinessException extends BaseException {

	//
    // Summary:
    //     Initializes a new instance of the BusinessException class with a specified
    //     error message.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    public BusinessException(String message) {
    	super(message);
    }
    
    //
    // Summary:
    //     Initializes a new instance of the BusinessException class with a specified
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
    public BusinessException(String paramName, Object paramValue, Throwable cause) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName, cause);
    	this(paramName, paramValue, null, cause);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.BusinessException class with a specified
    //     error message and the name of the parameter that causes this exception.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    //
    //   paramName:
    //     The name of the parameter that caused the current exception.
    public BusinessException(String paramName, Object paramValue) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, paramValue, null, null);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.BusinessException class with a specified
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
    public BusinessException(String paramName, Object paramValue, String module, Throwable cause) {
    	super(module, paramValue == null ? "exception.argument.null" : "exception.argument"
    		, new Object[] {paramName, paramValue}, "Invalid argument: " + paramName, cause);    	
    }
    
    public BusinessException(String paramName, Object paramValue, String module) {
        //super(module, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, paramValue, module, null);
    }
}
