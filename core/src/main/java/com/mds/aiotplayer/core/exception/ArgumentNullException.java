/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core.exception;

import com.mds.aiotplayer.i18n.util.I18nUtils;

/**
 * <p>EPerson: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
public class ArgumentNullException extends ArgumentException {
	
	/// <summary>
	///   The exception that is thrown when a general error occurs in the MDS.Web namespace.
	/// </summary>
	public ArgumentNullException(){
		super("Invalid parameter: NOT NULL");
		//super(I18nUtils.getMessage("Invalid parameter: NOT NULL"));
	}

	//
    // Summary:
    //     Initializes a new instance of the System.ArgumentNullException class with a specified
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
    public ArgumentNullException(String paramName, Throwable cause) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName, cause);
    	this(paramName, null, cause);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.ArgumentNullException class with a specified
    //     error message and the name of the parameter that causes this exception.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    //
    //   paramName:
    //     The name of the parameter that caused the current exception.
    public ArgumentNullException(String paramName) {
    	//super(null, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, null, null);
    }
    //
    // Summary:
    //     Initializes a new instance of the System.ArgumentNullException class with a specified
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
    public ArgumentNullException(String paramName, String module, Throwable cause) {
    	super(module, paramName, null, cause);    	
    }
    
    public ArgumentNullException(String paramName, String module) {
        //super(module, "exception.argument", new Object[] {paramName, paramValue}, "Invalid argument: " + paramName);
    	this(paramName, module, null);
    }
}
