/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.core.exception;

import com.mds.aiotplayer.common.exception.BaseException;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when a general error occurs in the MDS.Web namespace.
/// </summary>
public class WebException extends Exception{
	/// <summary>
	///   The exception that is thrown when a general error occurs in the MDS.Web namespace.
	/// </summary>
	public WebException(){
		super(I18nUtils.getMessage("exception.web_Ex_Msg"));
	}

	/// <summary>
	///   The exception that is thrown when a general error occurs in the MDS.Web namespace.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public WebException(String msg)	{
		super(msg);
	}

	/// <summary>
	///   The exception that is thrown when a general error occurs in the MDS.Web namespace.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public WebException(String msg, Throwable cause){
		super(msg, cause);
	}
}

