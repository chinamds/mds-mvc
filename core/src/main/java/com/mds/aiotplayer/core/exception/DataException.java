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

/**
 * <p>EPerson: Zhang Kaitao
 * <p>Date: 13-3-11 下午8:19
 * <p>Version: 1.0
 */
@SuppressWarnings("serial")
public class DataException extends BaseException {

	public DataException() {
    	super(I18nUtils.getMessage("exception.Data_Ex_Msg"));
    }
	
	//
    // Summary:
    //     Initializes a new instance of the System.DataException class with a specified
    //     error message.
    //
    // Parameters:
    //   message:
    //     The error message that explains the reason for the exception.
    public DataException(String message) {
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
	public DataException(String msg, Throwable cause)	{
		super(msg, cause);		
	}

}
