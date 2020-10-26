/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core.exception;

import com.mds.aiotplayer.common.exception.BaseException;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when MDS System encounters form data in a web page it does not recognize.
/// </summary>
@SuppressWarnings("serial")
public class UnexpectedFormValueException extends BaseException {

	/// <summary>
	///   Throws an exception to indicate an unexpected form data in a web page.
	/// </summary>
	public UnexpectedFormValueException(){
		super(I18nUtils.getMessage("exception.UnexpectedFormData_Ex_Msg"));
	}
			
	/// <summary>
	///   Throws an exception to indicate an unexpected form data in a web page.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public UnexpectedFormValueException(String msg)	{
		super(msg);
	}
	
	/// <summary>
	///   Throws an exception to indicate an unexpected form data in a web page.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public UnexpectedFormValueException(String msg, Throwable cause){
		super(msg, cause);
	}
}
