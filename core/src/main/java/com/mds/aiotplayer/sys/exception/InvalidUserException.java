/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.exception;

import com.mds.aiotplayer.common.exception.BaseException;
import com.mds.aiotplayer.i18n.util.I18nUtils;

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

