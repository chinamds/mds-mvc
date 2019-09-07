/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.core.exception;

import com.mds.common.exception.BaseException;
import com.mds.i18n.util.I18nUtils;

/**
 * <p>EPerson: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
public class ApplicationNotInitializedException extends BaseException {

	public ApplicationNotInitializedException() {
    	super(I18nUtils.getMessage("exception.applicationNotInitialized_Ex_Msg"));
    }
	
	//
    // Summary:
    //     Initializes a new instance of the System.ApplicationNotInitializedException class with a specified
    //     error message.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    public ApplicationNotInitializedException(String message) {
    	super(message);
    }

	/// <summary>
	///   Throws an exception to indicate MDS System has not been properly intialized.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public ApplicationNotInitializedException(String msg, Throwable cause)	{
		super(msg, cause);		
	}

}
