/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.exception;

import com.mds.common.exception.BaseException;
import com.mds.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when an invalid user is referenced, or one is attempted to be created
///   with invalid parameters.
/// </summary>
public class InvalidUserException extends Exception{
	/// <summary>
	///   Throws an exception to indicate when an invalid user is referenced, or one is attempted to be created
	///   with invalid parameters.
	/// </summary>
	public InvalidUserException(){
		super(I18nUtils.getMessage("exception.invalidUser_Ex_Msg"));
	}

	/// <summary>
	///   Throws an exception to indicate when an invalid user is referenced, or one is attempted to be created
	///   with invalid parameters.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public InvalidUserException(String msg)	{
		super(msg);
	}

	/// <summary>
	///   Throws an exception to indicate when an invalid user is referenced, or one is attempted to be created
	///   with invalid parameters.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public InvalidUserException(String msg, Throwable cause){
		super(msg, cause);
	}
}

